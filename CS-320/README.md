# CS 320: Software Testing, Automation, and Quality Assurance

Southern New Hampshire University, October 2026

This folder holds my portfolio artifacts from CS 320. The course was about writing JUnit tests that check code against customer requirements, then stepping back to think about testing strategy and mindset.

## What's Here

- **ContactService/**: From Project One. A contact object and an in-memory contact service for a mobile app, plus the JUnit tests that verify them.
  - `Contact.java`: Holds one contact and validates every field on the way in.
  - `ContactService.java`: Adds, deletes, and updates contacts by ID.
  - `ContactTest.java` and `ContactServiceTest.java`: 56 test runs, one section per requirement.
- **Byrne-Project02.docx**: From Project Two. My summary and reflections report on the testing approach, coverage, techniques, and mindset behind Project One.

## Reflection

### How can I ensure that my code, program, or software is functional and secure?

For functional, I write the tests from the requirements, not from my own code. If I test what I built, I just confirm my own assumptions. Every Contact requirement got its own test section, and every field got a null test, a one-over-the-limit test, and an exactly-at-the-limit test, since edges are where off-by-one bugs hide. Coverage helps, and Contact.java and ContactService.java both hit 100%, but coverage only proves the code ran. So I also broke the code on purpose, one change at a time, to see if the tests noticed. They caught 26 of 27 changes, and the one that got through behaved exactly like the original, so there was nothing to catch.

For secure, the big habit is validating every input before it gets stored, so bad data can never get in. Contact checks every field in its setters, the constructor reuses those setters, and the setters are final so a subclass can't sneak around them. The contact ID is final with no setter at all. Taking CS 305 at the same time drove home that a lot of security problems are just validation problems.

### How do I interpret user needs and incorporate them into a program?

I treat the requirements list as the source of truth and turn each line into something I can test. "Cannot be longer than 10 characters" becomes a 10 character test that has to pass and an 11 character test that has to fail. When a requirement is unclear, I try to ask instead of guess. The Contact requirements listed "PhoneNumber" as an updatable field when the field is called phone. I guessed they meant the same thing, and my tests verify that guess. On a real project that should be a question for the customer. At work I build from requirements documents on a government case management system, and asking early is a lot cheaper than finding out after release.

### How do I approach designing software?

Small pieces with one job each, and each rule in one place. Contact owns its validation, and ContactService just stores contacts in a HashMap and hands updates to Contact's setters. That made both classes easy to test, and it meant a rule only had to be fixed once. I also design with testing in mind from the start. If something is hard to test, that usually means the design needs work. Feedback matters too. My instructor suggested compiling the phone regex once as a constant and making constructor-called setters final, and I worked both in before the final project instead of letting them pile up as technical debt.
