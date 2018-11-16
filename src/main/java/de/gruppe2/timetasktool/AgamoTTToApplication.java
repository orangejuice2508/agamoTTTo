package de.gruppe2.timetasktool;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class AgamoTTToApplication extends SpringBootServletInitializer implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(AgamoTTToApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.password}")
    String dbpassword;

	@Override
	public void run(String... strings) throws Exception{
		log.info("Creating tables");
        log.info(dbpassword);

		jdbcTemplate.execute("DROP TABLE IF EXISTS `student`");
		jdbcTemplate.execute("CREATE TABLE `student` (" +
                "  `id` int(11) NOT NULL," +
                "  `first_name` varchar(45) DEFAULT NULL," +
                "  `last_name` varchar(45) DEFAULT NULL," +
                "  `email` varchar(45) DEFAULT NULL," +
                "  `time` TIMESTAMP," +
                "  PRIMARY KEY (`id`)" +
                ")");

		jdbcTemplate.execute("INSERT INTO `student` VALUES ('1', 'Test', 'Test', 'Test@Test.de', CURRENT_TIMESTAMP)");
	}
}
