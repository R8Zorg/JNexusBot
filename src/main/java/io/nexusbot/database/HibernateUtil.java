package io.nexusbot.database;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import jakarta.persistence.Entity;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    final static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Dotenv dotenv = Dotenv.load();

                Configuration configuration = new Configuration();
                configuration.setProperty("hibernate.connection.url", dotenv.get("DB_URL"));
                configuration.setProperty("hibernate.connection.username", dotenv.get("USER"));
                configuration.setProperty("hibernate.connection.password", dotenv.get("PASSWORD"));
                configuration.configure();

                try (ScanResult scanResult = new ClassGraph()
                        .acceptPackages("io.nexusbot.database")
                        .enableAnnotationInfo()
                        .scan()) {
                    List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Entity.class);
                    for (ClassInfo classInfo : classInfos) {
                        configuration.addAnnotatedClass(classInfo.loadClass());
                    }
                    StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties());
                    sessionFactory = configuration.buildSessionFactory(builder.build());

                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }
        return sessionFactory;
    }
}
