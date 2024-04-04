# Template for the Video Game Search Engine project

[![build workflow](https://github.com/lernejo/video_game_search_engine_template/actions/workflows/build.yml/badge.svg)](https://github.com/lernejo/video_game_search_engine_template/actions)
[![codecov](https://codecov.io/gh/lernejo/video_game_search_engine_template/branch/main/graph/badge.svg)](https://codecov.io/gh/lernejo/video_game_search_engine_template)

## Build the project

The project requires a JDK 17 (recommended distribution is Temurin from [Adoptium](https://adoptium.net/)).

From there, simply use the Maven wrapper to launch all tests (unit tests & integration tests).

`./mvnw verify`


// Function to load or create a case
fun loadOrCreateCase(
    process: StartableProcessEntity,
    startWorkflowSubmission: StartWorkflowSubmission,
    validatedSubmission: Map<String, Any?>,
): Case {
    // Get the client for interacting with cases based on the process engine id
    val casesClient = casesClients[process.engineId]

    // Get the case from the submission
    var case = startWorkflowSubmission.case

    // Log a trace message for checking if a case exists with a specific ID
    log.trace { "check if case with case id '${case?.id}' exists" }

    // Check if the case already has an ID
    if (case?.id != null) {
        // If the case has an ID, retrieve it from the cases client
        checkAndUpdateExistingCase(case.id)
    } else {
        // If the case doesn't have an ID, proceed to create a new case
        createOrFindNewCase(process, validatedSubmission)
    }

    // Return the case
    return case!!
}

// Function to check and update an existing case
fun checkAndUpdateExistingCase(caseId: String) {
    val retrieveCaseById = casesClient.caseApi.retrieveCaseById(caseId, false)
    // Check if the retrieved case is not in an opened status
    if (retrieveCaseById.status != CaseStatus.OPENED) {
        // If it's not in an opened status, throw an exception
        throw BadRequestException("Error while update case with id '${retrieveCaseById?.id}' : case is '${retrieveCaseById?.status}' ")
    }
}

// Function to create or find a new case
fun createOrFindNewCase(process: StartableProcessEntity, validatedSubmission: Map<String, Any?>) {
    val processConfig = process.getProcessConfig()
    val startWorkflowOnExistingCase = processConfig.startWorkflowOnExistingCase ?: false
    var createNewCase = false

    // Check if the process is configured to start a workflow on an existing case
    if (startWorkflowOnExistingCase) {
        createOrFindCaseOnExisting(process, validatedSubmission)
    } else {
        // If not starting a workflow on an existing case, create a new case
        createNewCase(process)
    }
}

// Function to create or find a case on an existing case
fun createOrFindCaseOnExisting(process: StartableProcessEntity, validatedSubmission: Map<String, Any?>) {
    // Get the business key field name from the process configuration
    val businessKey = validatedSubmission[processConfig.businessKeyFieldName!!]!!.toString()
    // Check if there is a workflow instance in progress with the same business key
    val instanceInProgress =
        casesClient.findOneWorkflowInstance(businessKey, status = WorkflowInstanceStatus.IN_PROGRESS)
    if (instanceInProgress != null) {
        // If there is an instance in progress with the same business key, throw an exception
        throw BadRequestException(
            "cannot create a workflow instance under an existing case, " +
            "there is already a workflowInstance that is in progress with the businessKey <$businessKey>"
        )
    }
    // Find the last workflow instance with the same business key
    val lastInstance = casesClient.findOneWorkflowInstance(businessKey, sort = "-createdAt")
    if (lastInstance != null) {
        // If there is a last instance found, assign its case ID to the case
        case = Case(id = lastInstance.caseId)
    } else {
        // If there is no last instance found, set the flag to create a new case
        createNewCase = true
    }
}

// Function to create a new case
fun createNewCase(process: StartableProcessEntity) {
    // Create a new case using the cases client API
    val createdCase = casesClient.caseApi.createCase(ModelCase())
    // Assign the ID of the newly created case to the case object
    case = Case(id = createdCase.id)
}
