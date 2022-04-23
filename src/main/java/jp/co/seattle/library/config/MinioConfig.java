package jp.co.seattle.library.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import io.minio.MinioClient;

@Configuration
@PropertySource({ "classpath:/minio.properties" })
public class MinioConfig {

	@Autowired
	private Environment environment;

	@Bean
	public MinioClient minioClient() {

		final MinioClient minioClient = MinioClient.builder()
				.endpoint(environment.getProperty("s3.url"), Integer.parseInt(environment.getProperty("s3.port")),
						false)
				.credentials(environment.getProperty("s3.access-id"), environment.getProperty("s3.secret-key"))
				.region("ap-northeast-1").build();
		return minioClient;
	}

	public String getMinioInfo(String key) {
		return environment.getProperty(key);

	}
}