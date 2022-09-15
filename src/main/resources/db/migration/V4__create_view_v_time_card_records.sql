create or replace view v_time_card_records as
select
	time_card_records.id as id,
	sections.code as section_code,
	sections.section_name as section_name,
	users.id as user_id,
	users.display_name as user_name,
	time_card_records.duty_date as duty_date,
	time_card_records.start_time as start_time,
	time_card_records.end_time as end_time,
	time_card_records.break_time as break_time,
	(time_card_records.end_time - time_card_records.start_time) as working_hours,
	((time_card_records.end_time - time_card_records.start_time) - time_card_records.break_time) as actual_working_hours,
	time_card_records.remarks as remarks,
	time_card_records.created_at as created_at

from
	time_card_records
		left join
			users
				on time_card_records.user_id = users.id
		left join
			sections
				on users.section_code = sections.code
;