select w.batch, 'ШПЗ' as spz,
to_char(create_date,'DD.MM.YYYY') as createdate,
w.gol_decnum||' - '||(select namepki from skladuser.pki pk where pk.pki = w.gol_decnum) as pkiinfo,
sklad
,w.prz_close
from skladuser.wo_request_list_view w
where w.sklad = :sklad
and w.prz_close = 0
order by to_date(create_date) desc