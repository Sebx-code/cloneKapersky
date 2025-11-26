rule KMSPico_Specific
{
    meta:
        description = "Détection spécifique et précise de KMSPico (réduit faux positifs)"
        author = "Mini Antivirus"
        version = "3.0"
        date = "2025-11-26"
        level = "balanced"

    strings:
        // Chaînes très spécifiques à KMSPico
        $kmspico1 = "KMSPico" nocase
        $kmspico2 = "KMSELDI" nocase
        $ratiborus = "By Ratiborus" nocase
        
        // Combinaisons de commandes suspectes
        $cmd1 = "slmgr /ato" nocase
        $cmd2 = "slmgr.vbs /skms" nocase
        
        // Patterns de registre KMS spécifiques
        $reg1 = "KeyManagementServiceName" nocase
        $reg2 = "KeyManagementServicePort" nocase
        
        // Signature binaire KMSPico (bytes très spécifiques)
        $bin1 = { 4B 4D 53 50 69 63 6F }    // "KMSPico" en hex
        $bin2 = { 4B 4D 53 45 4C 44 49 }    // "KMSELDI" en hex

    condition:
        // ✅ Conditions STRICTES pour éviter faux positifs
        filesize < 10MB and
        (
            // Doit avoir le nom exact ET des patterns
            ($kmspico1 and ($cmd1 or $cmd2)) or
            
            // Ou signature de Ratiborus avec commandes KMS
            ($ratiborus and $cmd1 and $cmd2) or
            
            // Ou patterns binaires spécifiques avec registre KMS
            (($bin1 or $bin2) and $reg1 and $reg2) or
            
            // Ou multiples indicateurs KMS forts
            ($kmspico2 and $reg1 and $reg2 and $cmd1)
        )
}

rule KMSPico_Executable
{
    meta:
        description = "Détection d'exécutables KMSPico par nom de fichier"
        author = "Mini Antivirus"
        
    condition:
        // ✅ Uniquement si le nom du fichier correspond EXACTEMENT
        filesize < 10MB and
        (
            filename matches /(?i)kmspico.*\.exe$/ or
            filename matches /(?i)kmseldi.*\.exe$/ or
            filename matches /(?i)kmsauto.*\.exe$/
        )
}