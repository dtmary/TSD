select v.PKI_CHG as PKI, v.QTY_CHG as CNT,
       mfg.Get_Cell_Pki(pk.pki, :sklad, 0) as cell,
       substr(pk.pki || '-' ||nvl(pk.typ_mod,pk.namepki),1,20) NAMEPKI
  from skladuser.vedom_zam v,
       skladuser.pki pk
where v.PKI_CHG = pk.pki
  and v.pki_SOST = :pki