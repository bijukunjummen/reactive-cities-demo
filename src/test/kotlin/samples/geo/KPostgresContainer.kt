package samples.geo

import org.testcontainers.containers.PostgreSQLContainer

class KPostgresContainer(imageName: String) : PostgreSQLContainer<KPostgresContainer>(imageName)