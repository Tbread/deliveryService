package com.practice.delivery.config

import org.jasypt.commons.CommonUtils
import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JasyptConfig {

    @Value("$(jasypt.encryptor.password)")
    private lateinit var password:String

    @Bean("jasyptStringEncryptor")
    fun stringEncryptor():StringEncryptor{
        var encryptor = PooledPBEStringEncryptor()
        var config = SimpleStringPBEConfig()
        config.password = password
        config.algorithm = "PBEWithMD5AndDES"
        config.keyObtentionIterations = 1000
        config.poolSize = 1
        config.providerName = "SunJCE"
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator")
        config.stringOutputType = CommonUtils.getStandardStringOutputType("base64")
        encryptor.setConfig(config)
        return encryptor
    }
}