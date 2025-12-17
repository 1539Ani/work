import groovy.json.JsonOutput

pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Collect Git Details') {
            steps {
                script {
                    def startTime = new Date(currentBuild.startTimeInMillis).toString()

                    def triggeredBy = currentBuild.rawBuild.getCauses().collect {
                        it.shortDescription
                    }.join(", ")

                    def changes = []
                    currentBuild.changeSets.each { changeSet ->
                        changeSet.items.each { entry ->
                            changes << [
                                commitId : entry.commitId,
                                author   : entry.author.fullName,
                                message  : entry.msg,
                                timestamp: new Date(entry.timestamp).toString(),
                                files    : entry.affectedFiles.collect { it.path }
                            ]
                        }
                    }

                    def gitBranch = env.GIT_BRANCH ?: scm.branches[0].name

                    def payload = [
                        source      : "jenkins",
                        job         : env.JOB_NAME,
                        build       : env.BUILD_NUMBER,
                        status      : currentBuild.currentResult,
                        branch      : gitBranch,
                        triggeredBy : triggeredBy,
                        buildStart  : startTime,
                        changes     : changes
                    ]

                    echo "===== JSON PAYLOAD ====="
                    echo JsonOutput.prettyPrint(JsonOutput.toJson(payload))
                    echo "========================"

                    httpRequest(
                        httpMode: 'POST',
                        url: 'https://YOUR_INSTANCE.service-now.com/api/your_event_source',
                        contentType: 'APPLICATION_JSON',
                        requestBody: JsonOutput.toJson(payload)
                    )
                }
            }
        }
    }
}

