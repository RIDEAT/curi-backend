package com.backend.curi.common.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AwsConfiguration {
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    @Primary
    @Bean
    public AWSStaticCredentialsProvider awsStaticCredentialsProvider() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(awsStaticCredentialsProvider())
                .withRegion(region)
                .build();
    }

    @Bean
    public AmazonSQS amazonSQSClient() {
        return AmazonSQSClientBuilder.standard()
                .withCredentials(awsStaticCredentialsProvider())
                .withRegion(region)
                .build();
    }

    @Bean
    public AmazonCloudFront amazonCloudFrontClient() {
        return AmazonCloudFrontClientBuilder.standard()
                .withCredentials(awsStaticCredentialsProvider())
                .withRegion(region)
                .build();
    }

    @Bean
    public AmazonSimpleEmailService getSESClient() {
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(awsStaticCredentialsProvider())
                .withRegion(region)
                .build();
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQSAsync) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSQSAsync);   // SQS API를 가지고 통신을 하는 container에 의해 사용되어지는 AmazonSQSAsync를 설정
        factory.setMaxNumberOfMessages(10);     // 한 번 poll하는 동안 조회되어져야하는 메세지의 최대 개수, 높을 수록 poll request 요청을 줄여준다.
        factory.setWaitTimeOut(20);             // queue에 메세지가 없을 때, queue로 들어오는 새로운 메세지에 대해 poll 요청 시 대기하는 timeout 시간
        factory.setTaskExecutor(messageThreadPoolTaskExecutor());   // 메세지를 poll하고 처리하는 handler 함수 설정
        return factory;
    }

    @Bean
    public ThreadPoolTaskExecutor messageThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("sqs-task-");
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }


}
