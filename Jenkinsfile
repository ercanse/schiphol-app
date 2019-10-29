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
        steps {
            sh '''
                echo "PATH = ${PATH}"
                echo "M2_HOME = ${M2_HOME}"
            '''
        }
    }

    stage('Test') {
        steps {
            sh 'mvn test'
        }
    }
}
