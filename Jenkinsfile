import groovy.json.JsonOutput
 
pipeline {
    agent any
 
    stages {
 
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
 
        stage('Collect Git Details & Send Webhook') {
            steps {
                script {
                    def startTime = new Date(currentBuild.startTimeInMillis).toString()
 
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
 
                    // def wrappedPayload = [ data: payload ]
 
                    echo "===== JSON PAYLOAD ====="
                    echo JsonOutput.prettyPrint(JsonOutput.toJson(payload))
                    echo "========================"
 
                    httpRequest(
                        httpMode: 'POST',
                        url: 'https://webhook.site/1fb88738-48cf-4bb9-8579-dfe5efbd72be',
                        contentType: 'APPLICATION_JSON',
                        requestBody: JsonOutput.toJson(payload)
                    )
                }
            }
        }
    }
}
