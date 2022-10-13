create or replace function if_table_exist(sch_name text, tbl_name text)
returns boolean
language plpgsql
as
$$
begin
   IF EXISTS 
	(
		SELECT 1
		FROM information_schema.tables 
		WHERE table_schema = sch_name
		AND table_name = tbl_name
	) THEN
		RETURN true;
	ELSE
		RETURN false;
	  END IF;
	  
end;
$$;

select "ok";