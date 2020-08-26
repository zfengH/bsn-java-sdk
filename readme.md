# PCN Gateway Java SDK Reference


## SDK in Java

### 1. Before calling

#### App parameter
>Application parameters are obtained from the application details page after the user participates in the application, or some parameters are set locally, specifically including
 * __Node gateway interface address：__ the calling address of the gateway nodeof the participating city 
 * __user number：__ the number of user
 * __app number：__ the number of participating applications 
 * __Public key ：__ the public key of the gateway node downloaded when user participates in the application
 * __Private key：__ the public key generated by BSN after the managed application logs in, and private key corresponding to the public key uploaded when  the unmanaged application logs in
 * __Https cert：__ The HTTPS cert used when calling the HTTPS gateway interface

 #### Local parameter
 * __cert directory：__ the directory used to store the user's private key and certificate generated by the unmanaged application when the user's certificate registration is invoked

### 2. Preparation

#### Import the SDK package
The following package needs to be introduced
```
import (
    "bsn-sdk-java.com.example.javacc.fabric"
    "bsn-sdk-java.com.example.javacc.entity.config.Config"
	)
```
#### Initialize config
An object can be initialized to store all the configuration information, which should be passed in at the time of invocation after being configured or read by the caller according to their respective project.
In config's 'Init' method, the operation to get the basic information of an App is realized. 
Please do not call this operation frequently, because this interface will occupy your TPS and traffic. 
You can use a static object to store 'config' in the project when you need it.Among them, the application private key, the node gateway public key, and the HTTPS certificate are stored in the directory 'resources', and only need to configure the path relative to the directory.
The certificate storage directory is the absolute path to the disk. You can modify the way child user certificates are stored by modifying the implementation in 'util. Keystore'.
```
	api:="" //Node gateway address
	userCode:="" //User ID
	appCode :="" //AppID
	puk :="cert/public_Key.pem" //Node gateway key
	prk :="cert/private_Key.pem" //Private key
	mspDir:="" //cert directory
	cert :="cert/bsn_gateway_https.crt" //cert
```
#### Initialize Config
Use the generated configuration object, call the following code to create a Config object to invoke the node gateway
```
	Config config = new Config();
	config.setAppCode(appCode );
	config.setUserCode(userCode);
	config.setCert(cert) 
	config.setPrk(prk)
	config.setApi(api);
	config.setPuk(puk);
	config.setMspDir(cert);
	config.initConfig(config);
```

####   Call interface
Each gateway interface has encapsulated the parameter object of the request and response, which can be directly called just by assigning the value, and the operation of signing and checking the signature has been realized in the method.
The following is the call operation for the registered child user, and others are similar.
```	
//Initialize config。
public void initConfig() throws IOException {
    Config config = new Config();
    config.setAppCode("app0001202004161020152918451");
    config.setUserCode("USER0001202004151958010871292");
    config.setApi("http://192.168.1.43:17502");
    config.setCert("cert/bsn_gateway_https.crt");
    config.setPrk("cert/private_Key.pem");
    config.setPuk("cert/public_Key.pem");
    config.setMspDir("D:/test");
    config.initConfig(config);
}
//Invoke the interface of user registration
public void userRegister() {
    try {
        initConfig(); //For example, in practice, the value needs to be called once during the program's life cycle
    } catch (IOException e) {
        e.printStackTrace();
        return ;
    }
    ReqUserRegister register = new ReqUserRegister();
    register.setName("test19");
    register.setSecret("123456");
    try {
        UserService.userRegister(register);
    } catch(GlobalException g) {
        g.printStackTrace();
    }catch (IOException e) {
        e.printStackTrace();
        return;
    }
}
```

### 3.Other instructions

#### Description of user ID cert on the unmanaged application

Since the user certificate needed by the unmanaged application when calling the gateway for transaction needs to be generated by the user himself, the process is: registered user -> registered user certificate. 
In the operation of registering a user certificate, a pair of secret keys are generated locally, the certificate's CSR file (certificate application file) is exported through the secret key, and the user certificate is invoked. 
The registration interface gets a valid certificate that can be used to normally initiate a transaction through the managed application transaction processing interface.
It should be noted that when setting CN in the CSR file, it is not the registered Name directly, but a Name spliced by Name and AppCode in the format of 'nam@appcode'.
This operation is implemented in the 'keyEscrowNoRegister' method of 'KeyEscrowFabric'.

__cert storage__ implemented through the `util.keystore` method, which stores only certificates in the form of local files if required 

#### About encryption

In order to facilitate data encryption and decryption in the chain operation of data transaction, the SDK implements symmetric encryption 'AES', 'DES' and an asymmetric encryption 'SM2' algorithm.
Symmetric encryption for 'AES' is specifically called as follows.
```

		String prk="";//Private key
        String content="";//encrypted content
        System.out.println("encrypted ciphertext："+AESEncode(encodeRules,content));
        System.out.println("decrypted ciphertext："+AESDncode(encodeRules,AESEncode(encodeRules,content)));
    /*
     * Encryption
     * 1.Construct KeyGenerator
     * 2.Initialize the key generator according to ecnodeRules
     * 3.Generate a secret key
     * 4.Create and initialze the password
     * 5.Content encryption
     * 6.Return string
     */
    public static String AESEncode(String encodeRules,String content){
        try {
            
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
           
            SecretKey original_key=keygen.generateKey();
            byte [] raw=original_key.getEncoded();
           
            SecretKey key=new SecretKeySpec(raw, "AES");
            
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte [] byte_encode=content.getBytes("utf-8");
            byte [] byte_AES=cipher.doFinal(byte_encode);
            String AES_encode=new String(new BASE64Encoder().encode(byte_AES));
            return AES_encode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Decryption
     * Steps：
     * 1.Step 1-4 with encryption
     * 2.The encrypted string is converted into a byte[] array
     * 3.Decrypt the encrypted content
     */
    public static String AESDncode(String encodeRules,String content){
        try {
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            keygen.init(128, new SecureRandom(encodeRules.getBytes()));
            SecretKey original_key=keygen.generateKey();
            byte [] raw=original_key.getEncoded();
            SecretKey key=new SecretKeySpec(raw, "AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] byte_content= new BASE64Decoder().decodeBuffer(content);
            /*
             * Decrypt
             */
            byte [] byte_decode=cipher.doFinal(byte_content);
            String AES_decode=new String(byte_decode,"utf-8");
            return AES_decode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

```
Asymmetric encryption 'SM2', the details are as follows. In this method, both the signature and the check sign of SM2 are realized
>Asymmetric encryption is encrypted by the public key and decrypted by the private key
```
	puk := ``//Public key
	prik := ``//Private key
	String src = "encrypted string";
    System.out.println("textUTF-8 to hex:" + Util.byteToHex(src.getBytes()));
    String SM2Enc = SM2Enc(puk, src);//crypted
    String SM2Dec = SM2Dec(prik, SM2Enc);//decrypt the private key
	
```


#### Secret key

In BSN, the key format of 'fabric' framework is' secp256r1 'curve of' ECDSA ', while the key format of 'fisco-bcos' framework is' SM2'
When a user connects to an unmanaged application, a key of the corresponding format needs to be generated and uploaded.
The generation of these two keys is described below. 
The generation of the secret key is generated using 'openssl', where the generation of 'SM2' secret key requires the '1.1.1' version of 'openssl' and above
> note: the following commands are executed in a Linux environment
##### 1. Key generation of ECDSA(secp256r1)
- Generate a private key
```
openssl ecparam -name prime256v1 -genkey -out key.pem
```
- Export the public key
```
openssl ec -in key.pem -pubout -out pub.pem
```
- Export the pkcs8 format private key 
> Since it is convenient to use the pkcs8 format key in some languages, you can export the pkcs8 format private key using the following command  
> The private key used in this SDK is in the form of pkcs8
```
openssl pkcs8 -topk8 -inform PEM -in key.pem -outform PEM -nocrypt -out key_pkcs8.pem
```
Three files can be generated from the above command
__`key.pem`__ :Private key  
__`pub.pem`__ :Public key  
__`key_pkcs8.pem`__ : private key in Pkcs8 format

##### 2.`SM2`secret key generation 

First you need to check whether the version of 'openssl' supports' SM2 'format secret key generation using  the following command
```
openssl ecparam -list_curves | grep SM2
```
Support if you output the following, 
```
SM2       : SM2 curve over a 256 bit prime field
```
Otherwise, you need to go to the official website to download '1.1.1' or above.
This is the version 1.1.1'. 
Download address：[https://www.openssl.org/source/openssl-1.1.1d.tar.gz](https://www.openssl.org/source/openssl-1.1.1d.tar.gz])  

- Generate a private key 
```
openssl ecparam -genkey -name SM2 -out sm2PriKey.pem
```
- Export a public key 
```
openssl ec -in sm2PriKey.pem -pubout -out sm2PubKey.pem
```
- Export the private key in pkcs8 format
> Since it is convenient to use the pkcs8 format key in some languages, you can export the pkcs8 format private key using the following command
> The private key used in this SDK is in the form of pkcs8
```
openssl pkcs8 -topk8 -inform PEM -in sm2PriKey.pem -outform pem -nocrypt -out sm2PriKeyPkcs8.pem
```
Three files can be generated from the above command
__`sm2PriKey.pem`__ :Private key 
__`sm2PubKey.pem`__ :Public key 
__`sm2PriKeyPkcs8.pem`__ :Private key in pkcs8 format



























