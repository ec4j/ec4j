#!/bin/bash
set -e

# ${githubUser}:${apiToken}, should be kept secret
# The API token can be created here: https://github.com/settings/tokens
# it should have at least the repo:status scope.
githubUserPassword="$1"

# A label that describes the current job, typically some sort of concatenation of OS and env.
jobLabel="$2"

# state is one of error, failure, pending, or success
state="$3"


case "$TRAVIS_EVENT_TYPE" in
pull_request)
    travisEventType="pr"
    ;;
*) echo "Job commit status will not be set for TRAVIS_EVENT_TYPE '${TRAVIS_EVENT_TYPE}'"
    exit 0
    ;;
esac


case "$state" in
error)
    description="${jobLabel} exited with an error"
    ;;
failure)
    description="${jobLabel} failed"
    ;;
pending)
    description="${jobLabel} is pending"
    ;;
success)
    description="${jobLabel} succeeded"
    ;;
*) echo "Unexpected state '${state}'"
    exit 1
    ;;
esac


# TRAVIS_COMMIT: The commit that the current build is testing.
# TRAVIS_REPO_SLUG: The slug (in form: owner_name/repo_name) of the repository currently being built.
# TRAVIS_TEST_RESULT: is set to 0 if the build is successful and 1 if the build is broken.
# TRAVIS_PULL_REQUEST_SHA:
#    if the current job is a pull request, the commit SHA of the HEAD commit of the PR.
#    if the current job is a push build, this variable is empty ("").

# POST /repos/:owner/:repo/statuses/:sha

body=$(cat <<EOF
{
  "state": "${state}",
  "target_url": "https://travis-ci.org/${TRAVIS_REPO_SLUG}/jobs/${TRAVIS_JOB_ID}",
  "description": "${description}",
  "context": "continuous-integration/travis-ci/${travisEventType}/${jobLabel}"
}
EOF
)

curl -u "${githubUserPassword}" --verbose -X POST --data "${body}" https://api.github.com/repos/${TRAVIS_REPO_SLUG}/statuses/${TRAVIS_PULL_REQUEST_SHA}
