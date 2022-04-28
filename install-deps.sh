cd lib

mvn install:install-file -Dfile=org.eclipse.cdt.core.native_6.1.100.202102030214.jar -DgroupId=org.eclipse.cdt.core -DartifactId=native -Dversion=6.1.100.202102030214 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.cdt.core_7.2.0.202102251239.jar -DgroupId=org.eclipse.cdt -DartifactId=core -Dversion=7.2.0.202102251239 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.jobs_3.10.1100.v20210111-0815.jar -DgroupId=org.eclipse.core -DartifactId=jobs -Dversion=3.10.1100.v20210111-0815 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.runtime_3.20.100.v20210111-0815.jar -DgroupId=org.eclipse.core -DartifactId=runtime -Dversion=3.20.100.v20210111-0815 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.equinox.common_3.14.100.v20210212-1143.jar -DgroupId=org.eclipse.equinox -DartifactId=common -Dversion=3.14.100.v20210212-1143 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.osgi.util_3.6.0.v20210212-1137.jar -DgroupId=org.eclipse.osgi -DartifactId=util -Dversion=3.6.0.v20210212-1137 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.osgi_3.16.200.v20210226-1447.jar -DgroupId=org.eclipse -DartifactId=osgi -Dversion=3.16.200.v20210226-1447 -Dpackaging=jar

cd ..
