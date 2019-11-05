node {
    stage('Checkout') {
        checkout([
            $class: 'GitSCM',
            branches: [[name: env.BRANCH_NAME]],
            extensions: [[$class: 'WipeWorkspace']],
            userRemoteConfigs: [[credentialsId: 'github-flight-track', url: 'https://github.com/S-Ercan/schiphol-app.git']]
        ])
    }

    stage ('Initialize') {
        sh '''
            echo "PATH = ${PATH}"
            echo "M2_HOME = ${M2_HOME}"
        '''
    }

    stage('Test') {
        sh 'mvn test'
    }

    stage('Package') {
        sh 'mvn package'
    }

    stage('Copy artifact') {
        sh 'cp target/schiphol* /home/ubuntu/artifacts/'
    }
}
