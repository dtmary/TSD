select v.PKI_CHG as PKI, round(v.QTY_CHG,3) as CNTFORMAT,
       mfg.Get_Cell_Pki(pk.pki, :sklad, 0) as cell,
       substr(pk.pki || '-' ||nvl(pk.typ_mod,pk.namepki),1,20) NAMEPKI,
       (select sum(ostatok) from skladuser.sklad s,
                                 skladuser.pkib pk
        where s.sklad = :sklad
          and s.pkib = pk.pkib
          and pk.pki = v.PKI_CHG
          and s.acc like '10%') as OST,
          v.QTY_CHG as CNTFULL,
          pk.kod_ei
  from skladuser.vedom_zam v,
       skladuser.pki pk
where v.PKI_CHG = pk.pki
  and v.pki_SOST = :pki