select t.docnum, t.indnum, t.headizd, t.part as pki, t.namepki, t.buyer, t.opnum, t.opsubnum, treb,
       sum(treb) over (partition by t.part) treball,
       (select z.spz
        from skladuser.rz_zakaz z
        where z.id_spz=t.id_spz) as shpz,
       mfg.Get_Cell_Pki(t.part, sklad, 0) as cell,
       (select nvl(sum(s.ostatok),0) as ost from skladuser.sklad s,
                      skladuser.pkib pk
        where s.pkib = pk.pkib
          and s.sklad = :sklad
          and pk.pki = part
          and pk.parent_pkib is null) as ost,
          '0' as otp,
          count(*) over (partition by t.part) cntzap
from (select t4.part, t4.opnum, t4.opsubnum, countopen as treb,
             substr(t4.part ||'-'||(select p.namepki
              from skladuser.pki p
              where p.pki=t4.part),1,18) as namepki,
             (select w.id_spz
              from skladuser.wo_ord w
              where w.opnum=t4.opnum) as id_spz,
             (select w.docnum
              from skladuser.wo_ord w
              where w.opnum=t4.opnum) as docnum,
             (select w.decnum
              from skladuser.wo_ord w
              where w.opnum=t4.opnum) as headizd,
             (select w.indnum
              from skladuser.wo_ord w
              where w.opnum=t4.opnum) as indnum,
             (select p.buyer
              from skladuser.pki p
              where p.pki=t4.part) as buyer,
             (select p.sklad
              from skladuser.pki p
              where p.pki=t4.part) as sklad
      from (select t4.opnum, t4.part, t4.opsubnum, round(countopen, 3) countopen, t4.count_req, t4.count_rsx
            from table(mfg.SubQuerySost133Treb(:batch, :company_id, :sklad, :buyer)) t4
            ) t4
            where countopen>0) t
where (t.headizd in (select ps_what
                           from (select CONNECT_BY_ROOT(t2.ps_decnumwhere) ps_decnumwhere, t2.ps_what
                                 from skladuser.wo_ps_struct_plan t2
                                 start with t2.ps_decnumwhere=:DECNUM
                                 connect by prior t2.ps_what=t2.ps_decnumwhere
                                 union
                                 select :DECNUM as ps_decnumwhere,
                                        :DECNUM as ps_what from dual
                        ))
             or (:DECNUM is null))
order by mfg.Get_Order_Cell_Pki(t.part, sklad), t.part, t.docnum, t.headizd
