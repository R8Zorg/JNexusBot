package io.nexusbot.database;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import jakarta.persistence.Entity;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String packagesPath;

    private HibernateUtil(String packagesPath) {
        HibernateUtil.packagesPath = packagesPath;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                try (ScanResult scanResult = new ClassGraph()
                        .acceptPackages(packagesPath)
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
