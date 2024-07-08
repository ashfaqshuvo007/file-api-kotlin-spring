# Summary
File-api is a technical task assignment for Mercans.

## Comments
Was the Acceptance Criteria easy to understand?

- It was fabulous, to be honest, one of the best I have come across in recent years during my job hunt here in Estonia
in terms of clarity. The boilerplate code really helped to just focus on the task at hand. The sample request and
response bodies saved much time.

- Despite all the details, if I understand correctly, you wanted some form fields like `size`, `name`, `contentType`, which
to me felt redundant as we could easily have gather all these info from the uploaded file resource object. I felt the field `meta`
was a necessary one.

## Which part of the assignment took the most time and why?

- Although, the boilerplate helped in some degree but getting the hang of Kotlin was a bit tricky for me as it is my
first encounter with it. Although, I implemented most of the business logic in java files but to utilize your
boilerplate kotlin code (mainly for logging, responseEntity and Exceptions), thorough knowledge of kotlin was required.
I do feel it would be easy to learn kotlin in a few weeks, but it seemed unnecessary exhaustion of my time to do so for
the sake of this task.

## What You learned

- After knowing about your system, honestly, this was the first time I learned that Kotlin is also usable for web
development. I used to think it is only for mobile devices.
- Also learned about some core spring framework classes and annotations like `@Document` instead of `@Entity`,
which was my goto earlier.
- Haven't used mongo db with spring before, so that was fascinating to interact with mongo.
- Really liked the Exception handling boilerplate and logging, definitely going to use these techniques in the future.

## TODOs

- Try to convert the java files into Kotlin
- Modify the ResponseEntity to allow file resource and headers
- Containerize the application
- CI/CD using gitlab
- Add uploadedFile type validation to uploadedFile upload endpoint
- Write comprehensive unit and integration tests
