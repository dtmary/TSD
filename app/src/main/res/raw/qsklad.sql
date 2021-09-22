select distinct sklad,
       (select s.naim from sklname s where s.sklad = w.sklad and s.company_id = w.company_id) naim
from skladuser.wo_request_list_view w
where w.prz_close = 0
order by sklad