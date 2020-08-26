package com.bsnbase.sdk.util.algorithm;

import com.bsnbase.sdk.util.common.UserCertInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoPrimitives;
import sun.security.pkcs10.PKCS10;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;

public class R1Algorithm implements AlgorithmTypeHandle {
    /**
     * @param stringPrivateKey
     * @param signString
     * @return
     */
    @Override
    public String sign(String stringPrivateKey, String signString) throws Exception {

        CryptoPrimitives cryptoPrimitives = new CryptoPrimitives();
        cryptoPrimitives.init();
        PrivateKey privateKey = cryptoPrimitives.bytesToPrivateKey(stringPrivateKey.getBytes());

        byte[] signature = cryptoPrimitives.sign(privateKey, signString.getBytes());

        return java.util.Base64.getEncoder().encodeToString(signature);
    }

    /**
     * @param pemCertificateString
     * @param signatureString
     * @param unsignedString
     * @return
     */
    @Override
    public boolean verify(String pemCertificateString, String signatureString, String unsignedString) throws Exception {

        CryptoPrimitives cryptoPrimitives = new CryptoPrimitives();
        cryptoPrimitives.init();
        Certificate publicKey = cryptoPrimitives.bytesToCertificate(pemCertificateString.getBytes());
        // 执行签名
        Signature signature = Signature.getInstance(Config.getConfig().getSignatureAlgorithm());
        // 验证签名
        signature.initVerify(publicKey);
        signature.update(unsignedString.getBytes());

        byte[] signByte = Base64.getDecoder().decode(signatureString);
        return signature.verify(signByte);
    }

    /**
     * 获取证书CSR
     *
     * @param DN
     * @return
     */
    @Override
    public UserCertInfo getUserCertInfo(String DN) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        //"SHA256withECDSA";
        String sigAlg = "SHA256withECDSA";
        int algSize = 256;
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA");
        kpg.initialize(algSize, new SecureRandom());
        KeyPair kp = kpg.generateKeyPair();
        Security.addProvider(new BouncyCastleProvider());
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        PKCS10 pkcs10 = new sun.security.pkcs10.PKCS10(publicKey);
        Signature signature = Signature.getInstance(sigAlg);
        signature.initSign(privateKey);
        @SuppressWarnings("restriction")
//        sun.security.x509.X500Name x500Name = new sun.security.x509.X500Name(DN);
//        pkcs10.encodeAndSign(x500Name, signature);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PrintStream ps = new PrintStream(baos);
//        pkcs10.print(ps);
//        String strPEMCSR = baos.toString();

        PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(new X500Name(DN), SubjectPublicKeyInfo.getInstance(kp.getPublic().getEncoded()));
        JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder(sigAlg);
        jcaContentSignerBuilder.setProvider("BC");
        ContentSigner contentSigner = jcaContentSignerBuilder.build(kp.getPrivate());
        PKCS10CertificationRequest csr =  builder.build(contentSigner);
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(csr);
        pemWriter.close();

        UserCertInfo user = new UserCertInfo();
        String strPEMCSR = stringWriter.toString();
        user.setCSRPem(strPEMCSR);
        user.setKey(privateKey);
        return user;
    }

}