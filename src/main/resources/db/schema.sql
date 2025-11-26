-- SQL schema for Mini Antivirus
CREATE DATABASE IF NOT EXISTS mini_antivirus;
USE mini_antivirus;

CREATE TABLE IF NOT EXISTS threats_signatures (
  hash VARCHAR(256) PRIMARY KEY,
  nom_malware VARCHAR(150),
  type VARCHAR(100),
  severite VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS quarantine (
  file_id INT AUTO_INCREMENT PRIMARY KEY,
  emplacement_original TEXT,
  date_isolation DATETIME,
  hash_detecte VARCHAR(256),
  stored_path VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS scan_history (
  id INT AUTO_INCREMENT PRIMARY KEY,
  date_scan DATETIME,
  fichiers_scannes INT,
  menaces_trouvees INT
);

-- Example insert signature (sha256 hash placeholder)
INSERT IGNORE INTO threats_signatures (hash, nom_malware, type, severite) VALUES
('e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855','TestMalware','Trojan','High'),

('d41d8cd98f00b204e9800998ecf8427e','Adware.FakeUpdate.A001','Adware','Low'),
('0cc175b9c0f1b6a831c399e269772661','Adware.FakeUpdate.A002','Adware','Low'),
('92eb5ffee6ae2fec3ad71c777531578f','Adware.FakeUpdate.A003','Adware','Low'),
('9f3c7a1dbbfc8952cfdcab12b9e4f4121f73c9acd0cd55022a0f15c1c8ebcda1','KMSpico.CrackTool.FakeKMS','CrackTool','High'),

('a14d1ef2b9b329bd3b4fb32d7c4115ea301458eaf19b8c2973c62a8917e8e2d1','Trojan.Agent.A001','Trojan','High'),
('e7d41a19a2e3d3ae0b1a9332c290fc5cc930e4cb2a7b8fb1568eb8e400c5a03b','Trojan.Agent.A002','Trojan','High'),
('c19cb31bb4a67a2d329e355c2f0257a8e3a6c7373d8231a7b69aa7ef25512e9a','Trojan.Agent.A003','Trojan','High'),

('bb4e9d1b918c4d932ee3a0eb4c02ad1fa49c55dbf9a62af3b37916c78e81fe20','Worm.AutoSpread.B001','Worm','Medium'),
('a4c3bc02d9127ab0fad1c18a5277bc4d345c314b97a98dc00332efb52ef3e5ab','Worm.AutoSpread.B002','Worm','Medium'),
('d2a833cda17a4752ea514c4f22db439bc93204f7ee82599e22c47e2312e2d13a','Worm.AutoSpread.B003','Worm','Medium'),

('e13b822001c58e71c6b63a36c2cce75465a3d8aaf75583a13c5da7e02f5d4c6f','Ransomware.Locky.C001','Ransomware','Critical'),
('1fc24e9cbf068dd41e98c724a9f3fcb34a2a8d7ddc83e7962a7d79724c2b9e44','Ransomware.Locky.C002','Ransomware','Critical'),
('77a9c2557350c3ad43e9b4f9d7b8bbcb6a870f549c3f912d77c4e78fd203ef81','Ransomware.Locky.C003','Ransomware','Critical'),

('b2c322f1ebc516ebc728f6857ee72b6cf19040ce21f83fa81e0fca1fb394a58b','Backdoor.Remote.D001','Backdoor','High'),
('0af3c2ef33aa1dc50f8b71bf9e9ce53f0b9a75ea1e22a13daf8f606a4dfa3d88','Backdoor.Remote.D002','Backdoor','High'),
('45ca13bb5f138a7ef3f7419f231cbb9dcd044e31621f4f7de3e94e0bf3f4e0bb','Backdoor.Remote.D003','Backdoor','High'),

('9f01e8bcee2c5ce73608206b2b9ad1bb76eac594351e2eaa7fdb82b9f457d72e','Rootkit.Hidden.E001','Rootkit','High'),
('a279ce1bf25effc9b73a2479ba793bcee664f645dd71bb8eac812f09f16f2f9a','Rootkit.Hidden.E002','Rootkit','High'),
('c526ccf17c82c584e42525fb5e1d8521bd1f1a2cc9db2e8e55341caea9f50a10','Rootkit.Hidden.E003','Rootkit','High'),

('4b92c9815a3f2eec1ddc2b8c6bcfa06c1e3a37366071e2ae63e3a5e3d84fb1f1','Stealer.Password.F001','Stealer','High'),
('f62bb8c5426d9a9f3217a35dfc6eb4a8c106df2d2edcaa4cd73b969ab3f7ef77','Stealer.Password.F002','Stealer','High'),
('29e53d1c5b1682b6b9e41dd349abbbd3ff8f57e7e3bfa8b5c03b7b9ceda59358','Stealer.Password.F003','Stealer','Medium'),

('5f1e88dd640d19cfb048e4c5ef65c2dfa4dea5e734a0aefb7c4b92ea4e394f3d','Exploit.PDF.G001','Exploit','Critical'),
('d31cbcd2a5d99cddafdc29c92a7cdb71f625fd4bc0e72c0c3ae604c4d2e2d29b','Exploit.PDF.G002','Exploit','Critical'),
('79bbcf2e73e8ffefef16f8cdb20352cda6a06a2ae61c8c52f8215a8b6d1cfa12','Exploit.PDF.G003','Exploit','Critical'),

('31bc24ef77310ab09961741b54622e1719b04d48f8a10a2b01bdd5f4508a2b65','Adware.Popup.H001','Adware','Low'),
('68f19ce05f3f2372eef45e0fd453cffd7123792ac7db582d33efd2c0e4ab8cd3','Adware.Popup.H002','Adware','Low'),
('8705e11f9d77678bfdc87439ff8d9ffda6efa21c66fe7e2a3ddc846a01c995d5','Adware.Popup.H003','Adware','Low'),

('a8d2c31fbf770c4cec8dc12e6d747d1f732bc2a97430f118f0f985924c4711ad','Spyware.Keylogger.I001','Spyware','High'),
('7ce873392a8e6df53b8e18a0ff9db038d235ab1e1c61fe2c8b3e63a8e8a2f88c','Spyware.Keylogger.I002','Spyware','High'),
('91463d449bcf1d4e3f8278a9af812bb79de5fb688c0c6a44edc4021df4e24f3a','Spyware.Keylogger.I003','Spyware','High'),
('d4f7e8c2b3a1e5f6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8g9h0','Virus.Generic.J001','Virus','Medium'),
('e5f6a7b8c9d0e1f2a3b4c5d6e7f8g9h0d4f7e8c2b3a1e5f6c7d8e9f0a1b2c3d4','Virus.Generic.J002','Virus','Medium'),
('f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8g9h0d4f7e8c2b3a1e5f6c7d8e9','Virus.Generic.J003','Virus','Medium'),

('c31d6ee2efa9ed22cebb74c9b802bca177b47badcc71df694e99c6af83c8f4f2','Spyware.Keylogger.I004','Spyware','Medium'),
('f478accd1eab28f9edeaa344b1b88ee2c97bb4e2fc934f92261bb8b13ce38f03','Spyware.Keylogger.I005','Spyware','High'),
('2a47fa9b7a11ae0fc33d7ad4e14507a69e08dfd69e36ab41d5862d7c3e44c261','Spyware.Keylogger.I006','Spyware','Medium'),

('4f73d1fe76b2bb89fc6fb91bc7da27144e37284a5543c18f736e6110ae25e921','Downloader.Injector.J001','Downloader','Medium'),
('b84c17ea491e6f72588e062bdea314877619fe4ab16f8038bc13fef7f3b647e1','Downloader.Injector.J002','Downloader','Medium'),
('d5f8315b97abded0f323ad9d8eef908c160e2b63c1e6b8d3827b719f748f8a98','Downloader.Injector.J003','Downloader','High'),

('ab1e71fe7c2489b92f96ff06c00322138b9c27cf3bbd0b386843d1a70e571a8b','Virus.FileInfector.K001','Virus','High'),
('f1bbdf37dbadc2048f7b2556de2afcd839a06536db5ae80bab12a0b88e5b1b63','Virus.FileInfector.K002','Virus','High'),
('2c9d437293cf6cf32eb72fb760d8f3f94dca74e721b566d8d274253287f22e62','Virus.FileInfector.K003','Virus','Medium'),

('071bcd125a5e067a0bbff6fd69304b3234a2cd8af7c3400fbbf1b273b3e82e41','Trojan.Dropper.L001','Trojan','High'),
('9ca53b736fba08bfdde1fb2032f3d1ebdfb157842f78cc5a7b3e19d9e3d9a435','Trojan.Dropper.L002','Trojan','High'),
('e6292e1fa04a60d5ad4460b9ef788c74ce4b1c03d3f04db97b4faa33a7a6a558','Trojan.Dropper.L003','Trojan','High'),

('8d5c69fd236e7fce86f5cf70f39829d633db1a7fef7dac013e8ce76e48b8a893','Ransomware.Crypto.M001','Ransomware','Critical'),
('7fb293d19461afa3ebe9e2d21f19fb124efcc397d92fecdf1a2d13fb0ebc577b','Ransomware.Crypto.M002','Ransomware','Critical'),
('0ac13f72a52a0c3b4ecb291d2cebcb740b44914f6132c764c30cd810aa2c1ec1','Ransomware.Crypto.M003','Ransomware','Critical'),

('276de54c93ee201bf9c2fcd2c8f29ef331e0e26577aec3a813b7ac48485dbf32','RAT.RemoteAccess.N001','RAT','High'),
('cce1d92f126e15bd058bd760f10a9dcdb332eea1e2474675e0cba4f0074bfe4b','RAT.RemoteAccess.N002','RAT','High'),
('f84815096f92aaacf6441cefa0c2d8a89c8a2b785958b57a2f470bdc724cd6e2','RAT.RemoteAccess.N003','RAT','High'),

('12987477a8c64b0cb648129f3a35eee98a0f77b283cb8b4e25a5c89b920ac896','Backdoor.Botnet.O001','Backdoor','Critical'),
('baf94a40a12baa3a38fd0f0d3c841110d9c06a6c19fb92f213e7e66ca6b61307','Backdoor.Botnet.O002','Backdoor','Critical'),
('e2bfbc447f081e6decce96e5828fdcf5e3bd53de87c6294026786fa6d4c1df7c','Backdoor.Botnet.O003','Backdoor','Critical'),

('bca06bc25499fa50c7cbe31d20571cba9cb9317d51d8962dfb6162adba8d2d82','Malware.Obfusc.P001','Obfuscated','Medium'),
('3c9bb43728ccb2602efc9fcb1f4cb2aa169f22a0f960bde2953e4129910ef9e8','Malware.Obfusc.P002','Obfuscated','Medium'),
('c2d9af143b8c3e4091e8612b57cdbd45df62589fd09343e4aeabd2a79c56dd42','Malware.Obfusc.P003','Obfuscated','High'),
('98dfce72a462241fcd850e27f287bfbae529d1ee3ac3ee1e6131d9bf7f80ef81','Malware.Obfusc.P004','Obfuscated','High'),

('1fa3b36eaae4f04fbc287a312f8c8a5633aa73856b113aef5a5df3fea73124b1','Trojan.Proxy.Q001','Trojan','Medium'),
('4da81ba40dc9cce2041b4deac87ec8ed0476e8ad32a1b32f39f3e1b8db46a1ea','Trojan.Proxy.Q002','Trojan','Medium'),
('bbcb3ce16589c829fa8bbaebc124a8b3f52cd896cd2bf8db5b2d7afadd2c6279','Trojan.Proxy.Q003','Trojan','High'),

('1176ab6db13823b8d69313fefa43965860b9d1d0c85ffb5bd60bc7f2fd5613f5','Trojan.SMS.R001','Trojan','Low'),
('4c4a6eb0cbfd2c4a117cc9bd84e399aa32cd5925c9405978cda5f7826c9162a8','Trojan.SMS.R002','Trojan','Medium'),
('d23abc35aeb07949c86fab852c07a9e35f4867076d453d60e1f169ac87a8c691','Trojan.SMS.R003','Trojan','Medium'),

('9b20db8460ebf2a6151b6fa9c3ba84e3d5df32d9c55ce1fd0a9b93689a187470','Spyware.Mobile.S001','Spyware','High'),
('ac4f004fa735d37bb5fbefac605de4be85375b035cfbbc5f66bdfd0750914c68','Spyware.Mobile.S002','Spyware','High'),
('0b5a4fbc14d6d5472d4d0953df1ad89f7700cb27ab0f64deabc5e402a5a81f24','Spyware.Mobile.S003','Spyware','Medium'),

('cc1b2e66f71d3adacd8b2b4a41f02dc91bffe222a39e8d4c0d86f1f4d7002e15','Spyware.Keylogger.I004','Spyware','Medium'),
('9122da8b633e9720675dc3f18dffcc09ee947c7da9a5d1e7d364a33e8fcae219','Spyware.Keylogger.I005','Spyware','Medium'),

('a22fb0cd78e218e445cd1299d1e8207c55b6d50ea76349ab4f2b743d22cf3a31','Botnet.Zeus.J001','Botnet','High'),
('f7d29790c9fdbe9a2d737cdce7d3c9afacb3ff2ad2a0a658ef1e0da8ec7d1b4f','Botnet.Zeus.J002','Botnet','High'),
('3b4dd0483a3a2e7cb808f8149fe3bb0d1a8e9b7e0d53beee018c3a8ecdc814e9','Botnet.Zeus.J003','Botnet','High'),

('10d219947ab2b8c8c98c88dd8bc9d35f8b6e955fa6c69a441107080b63f8c4f0','Trojan.Dropper.K001','Dropper','High'),
('a1d6e0b7747d1e28ec9f45ddb25c480799dd902156bf8c1820eb32cb7a1a49bd','Trojan.Dropper.K002','Dropper','High'),
('4947b77b8d93110916dbe0db62afdab2b92368f18a46502114ea12ddf7f193cb','Trojan.Dropper.K003','Dropper','High'),

('f12e4a8faf3460c9d9b5d12adb2ba36f93419090da1f0d8f792adc85ab87ee68','Trojan.Injector.L001','Injector','High'),
('8c8b93ee37184db3667fd8cf172f90fbb8b7b62f14a8b7af9c52c619b26a4c12','Trojan.Injector.L002','Injector','High'),
('b9b1699c25c112179eb81aa1c081f3346ad7529fd4ccf8869e4d637c8b8aae87','Trojan.Injector.L003','Injector','High'),

('3bd5b8aca537fadb82ec851384d4b03d3180aa2a8a06b8adfca5aeb2ebb15e2d','RAT.DarkComet.M001','RAT','Critical'),
('4cd33e9e6ab20a89220f54c4710a318a7cf259f271679823616c78cb11463baa','RAT.DarkComet.M002','RAT','Critical'),
('e4d98bc4bcb31c0a72f8d622c3f7f17bd8d017cc54b7630d681f212c39ee1655','RAT.DarkComet.M003','RAT','Critical'),

('901cb8a7b7007e34b5ba362cc1ab77a5e10c2546a73080b995462889825d2469','Miner.Crypto.N001','Miner','Medium'),
('4d120db075604c9b0cf59a464b3cd7a2374b0c259b5e05bc5795ff60ff6a0f6f','Miner.Crypto.N002','Miner','Medium'),
('c31e2d3395fb4ec8b5727b9d32f8a77817e326c604f290c3c95aa67f3d93478c','Miner.Crypto.N003','Miner','Medium'),

('0661e410f8980d9b4891255e8c89f8f22f1358dfb9fcc20fe91ec7a93b29c49d','Spyware.ScreenLogger.O001','Spyware','High'),
('864b9238f419f8cbbd2746e0fcbcfa7ff15ad2d3a4b02284d23e41d68f1a490d','Spyware.ScreenLogger.O002','Spyware','High'),
('9bd3dfb8fd05ef94cf764a7f6152be3cb4080f2c8c2c62d2e5e9d23803246ad7','Spyware.ScreenLogger.O003','Spyware','High'),

('aa9edfc363c9c73b87b4c8b3a6ac63a305305e4ec885a6ccaa8e0e9b802d3898','Trojan.Proxy.P001','Proxy','High'),
('f1f27c71abb88fd8db6fb2232298cc956a1e645b94397bae26644cd8b1f1d5c3','Trojan.Proxy.P002','Proxy','High'),
('3c7af9a4cc717e9c9d1745b3dde2c0e43d8cae03e84a4f16ba6adb1b7c46a1ca','Trojan.Proxy.P003','Proxy','High'),

('cbbe7b0e331ea7aac95b4c8856344932f2adbb2160d0784dfc9b0429c7f0e17f','Backdoor.Bot.Q001','Backdoor','High'),
('a3d472a0de98c45c6a95af41700a2d2d3520c93b36f0bb26e259ae7abf4bb7ac','Backdoor.Bot.Q002','Backdoor','High'),
('25b27adf88b1b5833631c6f73962c52cb5f4065a94db24331f0e768bbd6b8e32','Backdoor.Bot.Q003','Backdoor','High'),

('d5c7f3cd2396047c7bc9b7a0f49e921a04e3bcbcdc54381f4cd34bb96bf5e213','Ransomware.Revil.R001','Ransomware','Critical'),
('1c3365b3ef6b2a9d40742fcd4ed59b87fa7daf8256cf52089e55ff71e9a44a5b','Ransomware.Revil.R002','Ransomware','Critical'),
('a7db9bea0b50f47b950d9219f601a4112a41b347cd3225689db818b898812574','Ransomware.Revil.R003','Ransomware','Critical'),

('fa820a1bfe311ec02e9ba433d24bc3de4ba5aea5cf43113afd0b7b350c428f77','Worm.NetSky.S001','Worm','Medium'),
('6dfc872ea88f35e0ceaf7e29d44ef25f6cffa953a40d3d2bbf3baed81df0703d','Worm.NetSky.S002','Worm','Medium'),
('8b50a9d8ee2dc8a4417acdee9a20d94e1acbdfa833304afdfd734e9db5342acd','Worm.NetSky.S003','Worm','Medium'),

('a4789c5b74a8c376d1293f8f322e52e2e2c6f4dea79379f6d1e667feab7a91aa','Adware.Toolbar.T001','Adware','Low'),
('c13b9f4192412e801a8ae7e783df73c67c2295065ca8119cf02b5aeaacb7e2aa','Adware.Toolbar.T002','Adware','Low'),
('e49fd8f5c36feb5acbe245b25ae83eaaa674c41374c958a0cc04e4e15c3ee97f','Adware.Toolbar.T003','Adware','Low'),

('6d783a8df4ae2cc3672d5d1650293ee385431e4ba2f0b1ac3fdc52a05a4bb4de','Stealer.Cookie.U001','Stealer','Medium'),
('a2fbd98cf9c88bcb6519e6b7efcfcd7a8653bef1c670c9b0b2f6d987864f7e84','Stealer.Cookie.U002','Stealer','Medium'),
('d03ae262b4f795273d6cd7489175eb88481a4b4b022fa023dce715783d8795d1','Stealer.Cookie.U003','Stealer','Medium'),

('f624dc35faf350f18312b7cec4afb8de18adcb5a2b89ce4b1fbc8967d6af77fc','Dropper.AutoInstall.V001','Dropper','High'),
('73f1df8af763bda0a5de84263f1d0f86f9df810d7d3981e95ef6aef3f9b9eee8','Dropper.AutoInstall.V002','Dropper','High'),
('9b74ed18e3f915d3cd9bcfbeebd7776da3e2af82a41a14f79fa1822afc62ce49','Dropper.AutoInstall.V003','Dropper','High'),

('1dc37ffde42d343fc9fc3f2e56035797ebe11f9ce4f97d7c5ed4f2e0cfe2ad13','Exploit.Java.W001','Exploit','Critical'),
('8270e4f4df28253f4a3acbd0f34d2cd860efc8bf86c2be3ec9b91ebc3ceafb88','Exploit.Java.W002','Exploit','Critical'),
('df7994c3967450d8013618041b1c39f45e043f322b31deb4c20cd8bcb7ea7b52','Exploit.Java.W003','Exploit','Critical'),

('633f9ce14e7baaca3e1e38a3a99cee3f60cfa06d72fa4e3ebc0d1539e07d2d75','Spyware.Clipper.X001','Spyware','High'),
('ef2dc5f5c8c1a919c6cd1c6e2192ae36df8a8d98e22c9f63df3ae41731bdebc5','Spyware.Clipper.X002','Spyware','High'),
('a77c0f72f25d3ee7fda64263ad7be99ff7c591bbe3c9bee3f6628ff4c9b2a873','Spyware.Clipper.X003','Spyware','High'),

('105db89283f0ac243c47a6cbeb789579214cd19f06fc95cedfa8079060cfec5a','Backdoor.Platinum.Y001','Backdoor','High'),
('2f4dfac9ab4d6a3ca3c2de849f12f3bc6815cd1d578de41c4ecb937184c2edc3','Backdoor.Platinum.Y002','Backdoor','High'),
('de97b4e6f4aa422947a49f9d3cfe1bdfead3adc5da8db6673d8f8587d0c3e4b0','Backdoor.Platinum.Y003','Backdoor','High');


-- Migration helper: if `stored_path` column is missing in an existing installation, add it.
ALTER TABLE quarantine ADD COLUMN IF NOT EXISTS stored_path VARCHAR(512);
