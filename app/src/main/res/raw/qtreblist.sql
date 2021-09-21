select w.batch, 'ШПЗ' as spz,
to_char(create_date,'DD.MM.YYYY') as createdate,
'Информация по конструкторскому номеру' as pkiinfo
from skladuser.wo_request_list_view w
where w.sklad = '01222'
and w.prz_close = 0
order by opnum