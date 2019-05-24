# Trump Tech Test 

## Description
We want you to implement a REST API that, given a twitter username and a count N, returns the last N tweets shouted. Shouting a tweet consists of transforming it to uppercase and adding an exclamation mark at the end. We also want to get a cache layer of these tweets in order to avoid hitting Twitter's API (which let's imagine is very expensive) twice for the same username given a T time.

## Example 

Given these last two tweets from Donald Trump:
- "Big announcement with my friend Ambassador Nikki Haley in the Oval Office at 10:30am",
- "Will be going to Iowa tonight for Rally, and more! The Farmers (and all) are very happy with USMCA!"

The returned response should be:
```
curl -s http://oursuperawesometwittershoutapi.com/shout/realDonaldTrump?limit=2
[
    "BIG ANNOUNCEMENT WITH MY FRIEND AMBASSADOR NIKKI HALEY IN THE OVAL OFFICE AT 10:30AM!",
    "WILL BE GOING TO IOWA TONIGHT FOR RALLY, AND MORE! THE FARMERS (AND ALL) ARE VERY HAPPY WITH USMCA!"
]
```

## Constraints 
- Count N provided MUST be equal or less than 10. If not, our API should return an error.

## Code Provided
In order to get you started, we've provided some bootstrap code.
- `ShoutController` and `Starter`: Web server done in Akka HTTP.
- `TweetRepository` and `TweetRepositoryInMemory`: We want you to work on domain code. Therefore, we provide an in-memory implementation of the Twitter repository that returns random quotes about science 🧐. You don't need to implement real communication with Twitter, you can use `TwitterRepositoryInMemory` as a production implementation.
- `Tweet` domain model: Simple case class for modeling Tweets returned by `TweetRepository`.

You can run the webserver by running `runMain com.github.makiftutuncu.scalacandidatetest.Starter` inside the sbt console. By default it runs on port 9000.

![hello-world](/doc/img/helloworld.png)

Feel free to change any of the provided code or do it by yourself if you feel more comfortable.

## What will we evaluate?
* **Design:** We know this is a very simple application and can be solved with one line of code but we want to see how you design domain code. Let's pretend this is a super critical application for the company and you're going to maintain it (and make changes requested by the product owner) for years.
* **Testing:** We love automated testing and we love reliable tests. We like testing for two reasons: First, good tests let us deploy to production without fear (even on a Friday!). Second, tests give a fast feedback cycle so developers in dev phase know if their changes are breaking anything.
* **Simplicity**: We like separate code in domain, application and infrastructure layers. If our product owner asks us for the same application but accessed by command line (instead of the http server) it should be super easy!
