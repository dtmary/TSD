select pk.pki,
       pk.namepki,
       pk.sklad,
       (select sum(s.ostatok) from skladuser.pkib b,
                      skladuser.sklad s
        where s.pkib = b.pkib
          and s.sklad = pk.sklad
          and b.pki = pk.pki
          and b.parent_pkib is null) as ostatok
        from skladuser.pki pk
where pk.pki = :pki