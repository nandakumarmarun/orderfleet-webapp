DO $$
BEGIN 
  IF EXISTS 
(
	SELECT 1
	FROM information_schema.tables 
	WHERE table_schema = 'public'
	AND table_name = 'cicd_sample'
) THEN
PERFORM 'ok';
  END IF;
END $$;
