rule KMSPico_AllVariants
{
    meta:
        description = "Détection agressive de toutes les variantes d'activateurs KMS (KMSPico, KMSAuto, KMS Tools...)"
        author = "ChatGPT"
        version = "2.0"
        date = "2025-11-25"
        level = "aggressive"

    strings:
        // Indicateurs textuels ultra communs
        $t1 = "KMSPico" nocase
        $t2 = "KMS Pico" nocase
        $t3 = "KMSELDI" nocase
        $t4 = "AutoKMS" nocase
        $t5 = "KMSAuto" nocase
        $t6 = "KMS Tools" nocase
        $t7 = "KMS Emulator" nocase
        $t8 = "KMS activation" nocase
        $t9 = "KMSoffline" nocase
        $t10 = "By Ratiborus" nocase
        $t11 = "HEU KMS" nocase

        // Patterns de commandes caractéristiques
        $c1 = "slmgr /ato" nocase
        $c2 = "slmgr.vbs /ato" nocase
        $c3 = "slmgr.vbs /skms" nocase
        $c4 = "-skms" nocase
        $c5 = "sc stop sppsvc" nocase
        $c6 = "sc start sppsvc" nocase
        $c7 = "/sethst:" nocase
        $c8 = "/skms" nocase

        // Manipulation de registres Windows (KMS)
        $r1 = "Software\\Microsoft\\Windows NT\\CurrentVersion\\SoftwareProtectionPlatform" nocase
        $r2 = "KeyManagementServiceName" nocase
        $r3 = "KeyManagementServicePort" nocase

        // Patterns binaires évitant les repacks UPX
        $u1 = { 55 50 58 }                     // "UPX"
        $u2 = { 55 50 58 21 }                  // "UPX!"

        // Patterns binaires typiques KMSPico
        $b1 = { 4B 4D 53 }                     // "KMS"
        $b2 = { 2D 73 6B 6D 73 }               // "-skms"
        $b3 = { 2F 73 6B 6D 73 }               // "/skms"

        // Exécutables .NET (car beaucoup de versions sont en .NET)
        $dot1 = "mscorlib" nocase
        $dot2 = "System.Windows.Forms" nocase

    condition:
        filesize < 30MB and
        (
            5 of ($t*) or
            3 of ($c*) or
            2 of ($r*) or
            ( $t1 and 2 of ($c*) ) or
            ( $t3 and 2 of ($r*) ) or
            ( 2 of ($u*) and 3 of ($t*) ) or
            ( 3 of ($t*) and 2 of ($b*) ) or
            ( 3 of ($t*) and 1 of ($dot*) )
        )
}
