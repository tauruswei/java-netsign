package com.brilliance.netsign.controller;

import com.brilliance.netsign.config.redis.RedisService;
import com.brilliance.netsign.config.redis.SM2Key;
import com.brilliance.netsign.dao.KeyDao;
import com.brilliance.netsign.entity.Key;
import com.brilliance.netsign.model.*;
import com.brilliance.netsign.result.CodeMsg;
import com.brilliance.netsign.result.Result;
import com.brilliance.netsign.utils.CertificateUtils;
import com.brilliance.netsign.utils.CsrUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.brilliance.netsign.utils.LogTool.successLog;

/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description:
 * @Date: 2020/9/4 0004 21:56
 */
@Api(tags = "签名服务器模块", description = "签名服务器块 Rest API")
@RestController
@RequestMapping(value = "/netsign")
public class SBNetSignController {
    private static final Logger logger = LoggerFactory.getLogger(SBNetSignController.class);
    static String pri = "pri_";
    static String pub = "pub_";
    static int signCountSum = 0;
    static int verifyCountSum = 0;
    static int signCountSumTemp = 0;
    static int verifyCountSumTemp = 0;
    static int count = 0;
    static int time = 1 * 60;
    static DecimalFormat df = new DecimalFormat("0.00");

    //
//    static {
//        Runnable helloRunnable = new Runnable() {
//            public void run() {
//                System.out.println("");
//                System.out.println("===========================");
//                System.out.print("Sign count = " + signCountSum);
//                System.out.print("\t");
//                if(beginTime>0){
//                    System.out.println("Sign frequency = " +   df.format((float)signCountSum/((System.currentTimeMillis()-beginTime)/1000))+ "/s");
//                }
//                System.out.print("Verify count = " + verifyCountSum);
//                System.out.print("\t");
//                if(beginTime>0){
//                    System.out.println("Verify frequency = " +  df.format((float)verifyCountSum/((System.currentTimeMillis()-beginTime)/1000))+ "/s");
//                }
//                System.out.println("===========================");
//                System.out.println("");
//            }
//        };
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleAtFixedRate(helloRunnable, 0, 1 * 60, TimeUnit.SECONDS);
//    }
    static {
        Runnable helloRunnable = new Runnable() {
            public void run() {
                System.out.println("");
                System.out.println("===========================");
                System.out.print("Sign count = " + signCountSum);
                System.out.print("\t");
                if (signCountSumTemp >= 0) {
                    System.out.print("Sign frequency = " + df.format((float) (signCountSum - signCountSumTemp) / time) + "/s");
                    System.out.print("\t");
                    System.out.println("count = "+count);
                }
                signCountSumTemp = signCountSum;

                System.out.print("Verify count = " + verifyCountSum);
                System.out.print("\t");
                if (verifyCountSumTemp >= 0) {
                    System.out.println("Verify frequency = " + df.format((float) (verifyCountSum - verifyCountSumTemp) / time) + "/s");
                }
//                System.out.println("count = "+count);
                count+=1;
                verifyCountSumTemp = verifyCountSum;
                System.out.println("===========================");
                System.out.println("");
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, time, TimeUnit.SECONDS);
    }

    BouncyCastleProvider bc = new BouncyCastleProvider();
    @Autowired
    private KeyDao keyDao;
    @Autowired
    private RedisService redisService;

    @ApiOperation("创建p10请求")
    @PostMapping("/genP10")
    public Result genP10(@RequestBody RequestGenP10Model requestGenP10Model) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        if (requestGenP10Model.isCover()){
            keyDao.deleteById(pri + requestGenP10Model.getKeyLabel());
            keyDao.deleteById(pub + requestGenP10Model.getKeyLabel());
        }
        ResponseP10Model responseP10Model = null;
        try {
            responseP10Model = CsrUtils.generateCsr(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Key privateKey = new Key();
        privateKey.setKeyLabel(pri + requestGenP10Model.getKeyLabel());
        privateKey.setKeyMaterial(responseP10Model.getPriKey());
        int insert = 0;
        try{
            insert = keyDao.insert(privateKey);
        }catch (Exception e){
            e.printStackTrace();
            if(e.getMessage().contains("Duplicate entry")){
                return Result.error(CodeMsg.CREATE_KEY_PAIR_ERROR.fillArgs(e.getMessage()));
            }
        }
        if (insert == 1) {
            // 缓存 redis
            Map map = new HashMap<>();
            map.put("keyLabel", privateKey.getKeyLabel());
            map.put("keyMaterial", privateKey.getKeyMaterial());
            map.put("keyEnable", privateKey.getKeyEnable() + "");
            map.put("keyUser", "");
            redisService.hmset(SM2Key.getKey, pri + requestGenP10Model.getKeyLabel(), map);
        }

        Key publicKey = new Key();
        publicKey.setKeyLabel(pub + requestGenP10Model.getKeyLabel());
        publicKey.setKeyMaterial(responseP10Model.getPubKey());
        int insert1 = keyDao.insert(publicKey);
        if (insert1 == 1) {
            // 缓存 redis
            Map map = new HashMap<>();
            map.put("keyLabel", publicKey.getKeyLabel());
            map.put("keyMaterial", publicKey.getKeyMaterial());
            map.put("keyEnable", publicKey.getKeyEnable() + "");
            map.put("keyUser", "");
            redisService.hmset(SM2Key.getKey, pub + requestGenP10Model.getKeyLabel(), map);
        }
        return Result.success(responseP10Model);
    }

    @ApiOperation("上传证书")
    @PostMapping("/uploadCert")
    public Result uploadCert(@RequestBody RequestUploadCertModel requestUploadCertModel) throws IOException {



        Certificate cert = CertificateUtils.parseCertificate(requestUploadCertModel.getCert());
        String commonName = cert.getSubject().toString().split("CN=")[1];
//        System.out.println(cert.getTBSCertificate().getSerialNumber());
//        SubjectKeyIdentifier s = new BcX509ExtensionUtils().createSubjectKeyIdentifier(cert.getSubjectPublicKeyInfo());
//        System.out.println(s.getKeyIdentifier());
//        String subjectKeyId = cert.getTBSCertificate().getExtensions().getExtension(Extension.subjectKeyIdentifier).getExtnValue().toString();

//        String ski = subjectKeyId.split("#0427")[1];
//        char[] chars = ski.toCharArray();
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 1; i <chars.length ; i++) {
//            stringBuilder.append(chars[i]);
//            i++;
//        }
//        System.out.println(stringBuilder.toString());

        int update = keyDao.update(pri + requestUploadCertModel.getKeyLabel(), commonName);
        if (update == 1) {
            // 缓存 redis
            redisService.hset(SM2Key.getKey, pri + requestUploadCertModel.getKeyLabel(), "keyEnable", "1");
            redisService.hset(SM2Key.getKey, pri + requestUploadCertModel.getKeyLabel(), "keyUser", commonName);
            redisService.hset(SM2Key.getKey, pri + requestUploadCertModel.getKeyLabel(), "SignCount", "0");
        }
        int update1 = keyDao.update(pub + requestUploadCertModel.getKeyLabel(), commonName);
        if (update1 == 1) {
            // 缓存 redis
            redisService.hset(SM2Key.getKey, pub + requestUploadCertModel.getKeyLabel(), "keyEnable", "1");
            redisService.hset(SM2Key.getKey, pub + requestUploadCertModel.getKeyLabel(), "keyUser", commonName);
            redisService.hset(SM2Key.getKey, pub + requestUploadCertModel.getKeyLabel(), "VerifyCount", "0");
        }
        return Result.success(true);
    }

    @ApiOperation("签名")
    @PostMapping("/sign")
    public Result sign(@RequestBody RequestSignModel requestSignModel) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchProviderException, UnsupportedEncodingException, InvocationTargetException, IllegalAccessException {
        Map map = redisService.hgetall("SM2Key::" + pri + requestSignModel.getKeyLabel());
        Key priKey = new Key();
        BeanUtils.copyProperties(priKey, map);
        if (1 == priKey.getKeyEnable()) {
            KeyFactory keyFact = KeyFactory.getInstance("EC", bc);
            PrivateKey priv = keyFact.generatePrivate(new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(priKey.getKeyMaterial())));
            Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), new BouncyCastleProvider());
            signature.initSign(priv);

            // 写入签名原文到算法中
            signature.update(Base64Utils.decodeFromString(requestSignModel.getOrigBytes()));
            // 计算签名值
            byte[] signatureValue = signature.sign();
            synchronized(SBNetSignController.class){
                signCountSum += 1;
            }
            // 缓存总的签名次数
//            redisService.set(SM2Key.getKey, "signCountSum", signCountSum);
            // 缓存用户的签名次数
            String signCount = redisService.hget("SM2Key::" + pri + requestSignModel.getKeyLabel(), "SignCount");
            redisService.hset("SM2Key::" + pri + requestSignModel.getKeyLabel(), "SignCount", Integer.parseInt(signCount) + 1 + "");
            logger.info(successLog("SM2Sign",requestSignModel.getKeyLabel(),priKey.getKeyUser(),
                    requestSignModel.getOrigBytes(),Base64Utils.encodeToString(signatureValue)));
            return Result.success(Base64Utils.encodeToString(signatureValue));
        } else {
            return Result.error(CodeMsg.SIGN_VERIFY_ERROR);
        }
//            return Result.success("MEQCICqwlsKcRJrbAq8Oi32sRmVAGaiEUyvoUJLqDlniBxFcAiB6DPwboXAVyi28ccPjx5xRHzJBdfLTVVrqDYfVzt1jyA==");
    }

    @ApiOperation("验签")
    @PostMapping("/verify")
    public Result verify(@RequestBody RequestVerifyModel requestVerifyModel) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, InvocationTargetException, IllegalAccessException {
        Map map = redisService.hgetall("SM2Key::" + pub + requestVerifyModel.getKeyLabel());
        Key pubkey = new Key();
        BeanUtils.copyProperties(pubkey, map);
        if (1 == pubkey.getKeyEnable()) {
            KeyFactory keyFact = KeyFactory.getInstance("EC", bc);
            PublicKey publicKey = keyFact.generatePublic(new X509EncodedKeySpec(Base64Utils.decodeFromString(pubkey.getKeyMaterial())));
            Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), new BouncyCastleProvider());
            signature.initVerify(publicKey);
            signature.update(Base64Utils.decodeFromString(requestVerifyModel.getOrigBytes()));
            // 缓存总的验签次数
            synchronized(SBNetSignController.class) {
                verifyCountSum += 1;
            }
//            redisService.set(SM2Key.getKey, "verifyCountSum", verifyCountSum);
            // 缓存用户的验签次数
            String verifyCount = redisService.hget("SM2Key::" + pub + requestVerifyModel.getKeyLabel(), "VerifyCount");
            redisService.hset("SM2Key::" + pub + requestVerifyModel.getKeyLabel(), "VerifyCount", Integer.parseInt(verifyCount) + 1 + "");
            logger.info(successLog("SM2Verify",requestVerifyModel.getKeyLabel(),pubkey.getKeyUser(),
                    requestVerifyModel.getOrigBytes(),requestVerifyModel.getSignature()));
            return Result.success(signature.verify(Base64Utils.decodeFromString(requestVerifyModel.getSignature())));
        } else {
            return Result.error(CodeMsg.SIGN_VERIFY_ERROR);
        }
//            return Result.success(true);
    }

    @ApiOperation("签名/验签统计重置")
    @GetMapping("/reset")
    public Result reset() {
        signCountSum = 0;
        signCountSumTemp=0;
        verifyCountSum = 0;
        verifyCountSumTemp=0;
        count=0;
        return Result.success(null);
    }

    @ApiOperation("哈希")
    @PostMapping("/hash")
    public Result hash(@RequestBody RequestHashModel requestHashModel) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        MessageDigest digest = MessageDigest.getInstance("SM3", provider);
        String digestString = Base64Utils.encodeToString(digest.digest(Base64Utils.decodeFromString(requestHashModel.getMsgBytes())));
        return Result.success(digestString);
    }

    @ApiOperation("删除")
    @PostMapping("/deleteKeyPair")
    public Result delete(@RequestBody RequestDeleteModel requestDeleteModel) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        int i = keyDao.deleteById(pri + requestDeleteModel.getKeyLabel());
        int i1 = keyDao.deleteById(pub + requestDeleteModel.getKeyLabel());
        return Result.success(i == 1 & i1 == 1);
    }
}

