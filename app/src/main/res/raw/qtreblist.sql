select w.batch, 'ШПЗ' as spz,
to_char(create_date,'DD.MM.YYYY') as createdate,
'Информация по конструкторскому номеру' as pkiinfo,
sklad
from skladuser.wo_request_list_view w
where w.sklad = :sklad
and w.prz_close = 0
order by createdate desc