package com.brilliance.netsign.controller;

import com.brilliance.netsign.dao.KeyDao;
import com.brilliance.netsign.entity.Key;
import com.brilliance.netsign.model.*;
import com.brilliance.netsign.result.CodeMsg;
import com.brilliance.netsign.result.Result;
import com.brilliance.netsign.utils.CsrUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
    static String pri = "pri_";
    static String pub = "pub_";
    BouncyCastleProvider bc = new BouncyCastleProvider();
    @Autowired
    private KeyDao keyDao;

    @ApiOperation("创建p10请求")
    @PostMapping("/genP10")
    public Result genP10(@RequestBody RequestGenP10Model requestGenP10Model) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
//        if (true == requestGenP10Model.isCover()){
        keyDao.deleteById(pri + requestGenP10Model.getKeyLabel());
        keyDao.deleteById(pub + requestGenP10Model.getKeyLabel());
//        }
        ResponseP10Model responseP10Model = null;
        try {
            responseP10Model = CsrUtils.generateCsr(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Key privateKey = new Key();
        privateKey.setKeyLabel(pri + requestGenP10Model.getKeyLabel());
        privateKey.setKeyMaterial(responseP10Model.getPriKey());
        keyDao.insert(privateKey);
        Key publicKey = new Key();
        publicKey.setKeyLabel(pub + requestGenP10Model.getKeyLabel());
        publicKey.setKeyMaterial(responseP10Model.getPubKey());
        System.out.println(responseP10Model.getPubKey());
        keyDao.insert(publicKey);
        return Result.success(responseP10Model);

    }

    @ApiOperation("上传证书")
    @PostMapping("/uploadCert")
    public Result uploadCert(@RequestBody RequestUploadCertModel requestUploadCertModel) {
        keyDao.update(pri + requestUploadCertModel.getKeyLabel());
        keyDao.update(pub + requestUploadCertModel.getKeyLabel());
        return Result.success(true);
    }

    @ApiOperation("签名")
    @PostMapping("/sign")
    public Result sign(@RequestBody RequestSignModel requestSignModel) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchProviderException, UnsupportedEncodingException {
        Key priKey = keyDao.queryById(pri + requestSignModel.getKeyLabel());
        if (1 == priKey.getKeyEnable()) {
            KeyFactory keyFact = KeyFactory.getInstance("EC", bc);
            PrivateKey priv = keyFact.generatePrivate(new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(priKey.getKeyMaterial())));
            Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), new BouncyCastleProvider());
            signature.initSign(priv);

            // 写入签名原文到算法中
            signature.update(Base64Utils.decodeFromString(requestSignModel.getOrigBytes()));
            // 计算签名值
            byte[] signatureValue = signature.sign();
            return Result.success(Base64Utils.encodeToString(signatureValue));
        } else {
            return Result.error(CodeMsg.SIGN_VERIFY_ERROR);
        }
    }

    @ApiOperation("验签")
    @PostMapping("/verify")
    public Result verify(@RequestBody RequestVerifyModel requestVerifyModel) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Key pubkey = keyDao.queryById(pub + requestVerifyModel.getKeyLabel());
        if (1 == pubkey.getKeyEnable()) {

            KeyFactory keyFact = KeyFactory.getInstance("EC", bc);
            PublicKey  publicKey = keyFact.generatePublic(new X509EncodedKeySpec(Base64Utils.decodeFromString(pubkey.getKeyMaterial())));
            Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), new BouncyCastleProvider());
            signature.initVerify(publicKey);
            signature.update(Base64Utils.decodeFromString(requestVerifyModel.getOrigBytes()));

            return Result.success(signature.verify(Base64Utils.decodeFromString(requestVerifyModel.getSignature())));
        } else {
            return Result.error(CodeMsg.SIGN_VERIFY_ERROR);
        }
    }

    @ApiOperation("测试get方法")
    @GetMapping("/index")
    public void index() {
        System.out.println("test get method");
    }

    @ApiOperation("哈希")
    @PostMapping("/hash")
    public Result hash(@RequestBody RequestHashModel requestHashModel) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        MessageDigest digest = MessageDigest.getInstance("SM3", provider);
        String digestString=
                 Base64Utils.encodeToString(digest.digest(Base64Utils.decodeFromString(requestHashModel.getMsgBytes())));
        return Result.success(digestString);
    }
    @ApiOperation("删除")
    @PostMapping("/deleteKeyPair")
    public Result delete(@RequestBody RequestDeleteModel requestDeleteModel) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidKeyException, SignatureException {
        int i = keyDao.deleteById(pri + requestDeleteModel.getKeyLabel());
        int i1 = keyDao.deleteById(pub + requestDeleteModel.getKeyLabel());
        return Result.success(i==1&i1==1);
    }
}

