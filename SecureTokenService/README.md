Build Instructions
========================================

### Prerequisites
* JDK 1.6
* Maven 

### Build with LDAP backend

```mvn clean package -P profile-ldap```

### Build with NBIA backend

```mvn clean package -P profile-nbia```

### Build with Dorian backend

```mvn clean package -P profile-dorian```

