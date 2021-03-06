-- Procedure for creating new seats
CREATE OR REPLACE PROCEDURE createSeats (roomId number, rowsNumber number, seatsInRowNumber number)
AS
   curRow number;
   curCol number := 0;
   typeSeat varchar2(40);
BEGIN
    typeSeat := 'comfort';
    for curRow in 1 .. rowsNumber
    loop
        for curCol in 1 .. seatsInRowNumber
        loop
            insert into seats values (default, roomId, curCol, curRow, typeSeat, default);
        end loop;
    end loop;
    typeSeat := 'luxury';
    update seats
    set type = typeSeat where room_id = roomId and positionY > 4 / 5 * rowsNumber;
END;
/


-- Procedure for setting available = 0 for tickets when schedule not available
CREATE OR REPLACE PROCEDURE update_tickets_status is
BEGIN
    update tickets
    set available = 0 where schedule_id in (
        select s.schedule_id from schedules s where s.available = 0
    );
END;
/


-- Create seats for new room with 1/5 'luxury' type seats
CREATE OR REPLACE TRIGGER tg_add_seats
AFTER INSERT on rooms FOR EACH ROW WHEN (new.rowsNumber is not null and new.seatsInRowNumber is not null)
BEGIN
    createSeats(:new.room_id, :new.rowsNumber, :new.seatsInRowNumber);
END;
/


-- Delete seats when room is deleted
CREATE OR REPLACE TRIGGER tg_delete_seats
AFTER UPDATE OF available on rooms FOR EACH ROW when (old.available != new.available)
BEGIN
    update seats set available = 0 where room_id = :old.room_id;
END;
/


-- Update number of seats if it was changed
CREATE OR REPLACE TRIGGER tg_update_seats
AFTER UPDATE on rooms FOR EACH ROW WHEN ((new.rowsNumber is not null and new.seatsInRowNumber is not null) and
                                         (new.rowsNumber != old.rowsNumber or new.seatsInRowNumber != old.seatsInRowNumber))
BEGIN
    delete from seats where room_id = :new.room_id;
    createSeats(:new.room_id, :new.rowsNumber, :new.seatsInRowNumber);
END;
/


--Function to check if we can add a schedule without conflict. 1: if we can, 0: if we can't
create or replace function can_create_schedule(mv_id number, rm_id number, date_start number, open_sale number, close_sale number)
return number
as
    date_end    number;
    tmp_start   number;
    tmp_end     number;
begin
    select length*60*1000 + date_start into date_end
    from movies
    where mv_id = movie_id;
    for schedule in (select * from schedules ss where rm_id = ss.room_id and ss.available = 1 and (
        select mm.available from movies mm where mm.movie_id = ss.movie_id
    ) = 1)
    loop
        select schedule.datetime into tmp_start from dual;
        tmp_end := tmp_start;
        select schedule.datetime + m.length*60*1000 into tmp_end
        from movies m where m.movie_id = schedule.movie_id and m.available = 1;
        if (date_start between tmp_start and tmp_end) or (date_end between tmp_start and tmp_end) then
            return 0;
        end if;
    end loop;
    return 1;
end;
/
----Function to return status of seats in a schedule
create or replace type seat_record as object (
       seat_id number,
       positionx number,
       positiony number,
       type varchar2(40),
       is_free number
);
/
create or replace type seats_table as table of seat_record;
/

create or replace function get_seats(sch_id number)
return seats_table
as
    rm_id number;
    res seats_table;
begin
    select room_id into rm_id from schedules where available = 1 and schedule_id = sch_id;
    select seat_record(s.seat_id, s.positionx, s.positiony, s.type,
    (
        select 1-count(s.seat_id) from tickets t
        where t.available = 1 and t.schedule_id = sch_id and s.seat_id = t.seat_id
    ) )
    bulk collect into res from seats s where room_id = rm_id and available = 1;
    return res;
end get_seats;
/