select v.PKI_CHG as pkizam, v.QTY_CHG as cntzam,
       mfg.Get_Cell_Pki(pk.pki, :sklad, 0) as cell,
       nvl(pk.typ_mod,pk.namepki) nnamezam
  from skladuser.vedom_zam v,
       skladuser.pki pk
where v.PKI_CHG = pk.pki
  and v.pki_SOST = :pki