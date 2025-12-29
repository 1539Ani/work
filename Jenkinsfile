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

                    // SAFE method (no rawBuild)
                    def triggeredBy = currentBuild.getBuildCauses()
                            .collect { it.shortDescription }
                            .join(", ")

                    def gitBranch = env.GIT_BRANCH ?: 'origin/master'

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

                    // Wrap payload inside "data" object
                    def wrappedPayload = [ data: payload ]

                    echo "===== JSON PAYLOAD ====="
                    echo JsonOutput.prettyPrint(JsonOutput.toJson(wrappedPayload))
                    echo "========================"

                    // Your test webhook
                    httpRequest(
                        httpMode: 'POST',
                        url: 'https://techmtriggersdev.service-now.com/api/sn_jenkinsv2_spoke/testjenkins?X-SkipCookieAuthentication=true&jenkins-token=now_lNgxLGgf_qQ_HU_BrOUFY5jriG4g9yh0KyHfQzOtWeAyIlWqGO7GDR96LNYkHyHLxVgsg30RFesdfTBx35pvug',
                        contentType: 'APPLICATION_JSON',
                        requestBody: JsonOutput.toJson(wrappedPayload)
                    )
                }
            }
        }
    }
}
