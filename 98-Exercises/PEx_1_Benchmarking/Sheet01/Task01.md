# Benchmarking Task01a:
## Task01.a
- Scenario: Movie fan of Sergio Leone western that would like also to see other western movies.
- - Query01_keywords: "sergio leone"
- - Query02_question: "What are Western Movies from or like Sergio Leone?"
- - Query03_recommendation: "Movies like 'The good the bad and the ugly' "
- Queries using the Notebook:
- - Set up the data set to 3000 samples.
- - Run the notebook with the queries 01-03
- - Feed the print of the dataset and the query to chatgpt.
## Task01.a. Query01:
- query = "sergio leone"
- Notebook ouput with query:
- - You are assessing movies. Given the query below and the results, assess which movies are relevant for the query

Query: sergio leone

Results:
ID: tt0087843
Title: Once Upon a Time in America (1984)
Summary: Crime, passion and lust for power - Sergio Leone's explosive saga of gangland America.. A former Prohibition-era Jewish gangster returns to the Lower East Side of Manhattan over thirty years later, where he once again must confront the ghosts and regrets of his old life.
Genres: [Drama, Crime]
Cast: Robert De Niro as David 'Noodles' Aaronson, James Woods as Maximilian 'Max' Bercovicz, Elizabeth McGovern as Deborah Gelly, Joe Pesci as Frankie Minaldi, Burt Young as Joe, Danny Aiello as Police Chief Vincent Aiello, William Forsythe as Philip 'Cockeye' Stein, James Russo as Bugsy, Louise Fletcher as Cemetery Director (uncredited)
- - Output of chatgpt:
Assessment: Relevant

Once Upon a Time in America (1984) — Relevant
This film was directed by Sergio Leone and is one of his most important and well-known works. The query directly matches the filmmaker associated with the movie.
- - Notes: The query is restricted due to the output of the script which gives us just one movie.
## Task01.a. Query02:
- query = "What are Western Movies from or like Sergio Leone?"
- Notebook ouput with query:
- - You are assessing movies. Given the query below and the results, assess which movies are relevant for the query

Query: What are Western Movies from or like Sergio Leone?

Results:
no results found
- - Output of chatgpt:
Assessment: No Relevant Movies

No results found — Not relevant
Since there are no movies listed in the results, there are no assessable or relevant movies for the query “What are Western Movies from or like Sergio Leone?”"
- - Notes: The notebook could not handle the question with the "set of words approach."
- - Second try: Input chatgpt the precision-recall notebook and ask what I can change to achieve a senseful query.
- Input to chatgpt: Given the code what can I change to achieve a better query result?
- TL DR: change these for better query
- - stopwords_checkbox.value = True
- - query = "sergio leone western"
- - result = index.query_or(pipeline.set_of_words(query))

### Full Output Chatgpt:
Your code is **working as designed**, but the *query formulation + retrieval model* are the reason you get **no results** for:

> *“What are Western Movies from or like Sergio Leone?”*

Below are **concrete, actionable changes** you can make to get much better results.

---

## 1. The core problem (why this query fails)

You are using a **Boolean AND retriever**:

```python
result = index.query_and(pipeline.set_of_words(query))
```

This means:

* **ALL query terms must appear in a movie document**
* Your query contains many *non-informative words*:

```
what, are, from, or, like
```

No movie description contains *all* of those words → **empty result set**.

---

## 2. First fix (most important): remove stopwords ✅

### Change:

```python
stopwords_checkbox = widgets.Checkbox(
    value=True,  # CHANGE THIS
    description='Remove Stopwords',
)
```

Then **rebuild the collection**.

### Why this helps

Your query becomes closer to:

```
western movies sergio leone
```

instead of:

```
what are western movies from or like sergio leone
```

This alone will dramatically improve recall.

---

## 3. Second fix: don’t use Boolean AND for natural-language queries

Boolean AND is **too strict** for exploratory questions.

### Replace:

```python
result = index.query_and(pipeline.set_of_words(query))
```

### With (better):

```python
result = index.query_or(pipeline.set_of_words(query))
```

### Why

* `AND` → intersection (very small)
* `OR` → union (much better recall)

For recommendation-style queries (“from or like”), **OR is the correct choice**.

---

## 4. Third fix: simplify the query itself

Your system is **not a QA model**, it’s a keyword retriever.

### Instead of:

```python
query = "What are Western Movies from or like Sergio Leone?"
```

Use:

```python
query = "sergio leone western"
```

or:

```python
query = "spaghetti western sergio leone"
```

This aligns with how your index works.

---

## 5. Optional improvement: use TF-IDF ranking (best practice)

Right now:

* Boolean retrieval → no ranking
* All matches treated equally

If your `BooleanRetriever` supports it, consider:

* Switching to a **TF-IDF cosine similarity retriever**
* Or post-ranking Boolean results using `features_tfidf`

This would surface:

* *The Good, the Bad and the Ugly*
* *Once Upon a Time in the West*
* *For a Few Dollars More*

even if “western” is missing from some summaries.

---

## 6. Minimal set of changes that will fix your issue

### ✅ Do these three things:

1. **Enable stopword removal**
2. **Use `query_or` instead of `query_and`**
3. **Simplify the query text**

### Final example:

```python
stopwords_checkbox.value = True
query = "sergio leone western"
result = index.query_or(pipeline.set_of_words(query))
```

---

## 7. Conceptual takeaway (important)

| Query type       | Best retrieval      |
| ---------------- | ------------------- |
| Fact lookup      | Boolean AND         |
| Recommendation   | Boolean OR / TF-IDF |
| Natural language | Stopwords + ranking |

Your query is a **recommendation query**, but your system was configured for **exact matching**.

---

If you want, I can:

* Refactor this into a **hybrid Boolean + TF-IDF retriever**
* Suggest a **query rewriting step**
* Help tune stemming vs stopwords tradeoffs

Just tell me 👍

---

## After changing the code according to chatgpt:
- query = "What are Western Movies from or like Sergio Leone?"
- with changes in the code:
- - stopwords_checkbox.value = True
- - query = "sergio leone western"
- - result = index.query_or(pipeline.set_of_words(query))
- Notebook ouput with query:
- - You are assessing movies. Given the query below and the results, assess which movies are relevant for the query

Query: sergio leone western

Results:
ID: tt0112346
Title: The American President (1995)
Summary: Why can't the most powerful man in the world have the one thing he wants most?. Widowed U.S. president Andrew Shepherd, one of the world's most powerful men, can have anything he wants -- and what he covets most is Sydney Ellen Wade, a Washington lobbyist. But Shepherd's attempts at courting her spark wild rumors and decimate his approval ratings.
Genres: [Comedy, Drama, Romance]
Cast: Michael Douglas as Andrew Shepherd, Michael J. Fox as Lewis Rothschild, Martin Sheen as A.J. MacInerney, David Paymer as Leon Kodak, Richard Dreyfuss as Senator Bob Rumson, John Mahoney as Leo Solomon

ID: tt0114938
Title: Wild Bill (1995)
Summary: The Name Is Legendary. The Man Is Real.. Biopic about famous gunslinger Wild Bill Hickock. The early career of legendary lawman is telescoped and culminates in his relocation in Deadwood and a reunion with Calamity Jane.
Genres: [Action, Western]
Cast: Jeff Bridges as James Butler 'Wild Bill' Hickok, Ellen Barkin as Calamity Jane, John Hurt as Charley Prince, David Arquette as Jack McCall, Bruce Dern as Will Plummer, James Remar as Donnie Lonigan

ID: tt0110413
Title: Leon: The Professional (1994)
Summary: If you want a job done well, hire a professional.. Leon, the top hit man in New York, has earned a rep as an effective "cleaner". But when his next-door neighbors are wiped out by a loose-cannon DEA agent, he becomes the unwilling custodian of 12-year-old Mathilda. Before long, Mathilda's thoughts turn to revenge, and she considers following in Leon's footsteps.
Genres: [Thriller, Crime, Drama]
Cast: Jean Reno as Léon Montana, Gary Oldman as Norman Stansfield, Danny Aiello as Tony, Michael Badalucco as Mathilda's Father, George Martin as Receptionist

ID: tt0114210
Title: A Pyromaniac's Love Story (1995)
Summary: . A pastry boy and the son of a hair-piece mogul become involved in an arson scandal. Sergio is offered a bribe in exchange for taking the blame for the fire that destroys his workplace. Garet, the real arsonist, is apalled that someone else would try to take credit for his act of love. Before long, Sergio and Garet become entangled in a zany love-quadrangle involving Hattie and Stephanie. Written by Brian Whiting
Genres: [Comedy, Drama, Romance]
Cast: John Leguizamo as Sergio, Michael Lerner as Perry, Armin Mueller-Stahl as Mr. Linzer, Joan Plowright as Mrs. Linzer, Mike Starr as Sgt. Zikowski

ID: tt0105652
Title: Pushing Hands (1992)
Summary: . The story is about an elderly Chinese tai chi chuan teacher and grandfather who emigrates from Beijing to live with his son, American daughter-in-law, and grandson in a New York City suburb. The grandfather is increasingly distanced from the family as a "fish out of water" in Western culture.
Genres: [Comedy, Drama]
Cast: 

ID: tt0114214
Title: The Quick and the Dead (1995)
Summary: Think you are quick enough?. A mysterious woman comes to compete in a quick-draw elimination tournament, in a town taken over by a notorious gunman.
Genres: [Action, Western]
Cast: Sharon Stone as Ellen "The Lady“, Gene Hackman as John Herod, Russell Crowe as Cort, Leonardo DiCaprio as Fee Herod "The Kid“, Keith David as Sgt. Cantrell, Lance Henriksen as Ace Hanlon, Pat Hingle as Horace, Mark Boone Junior as Scars

ID: tt0111255
Title: The Specialist (1994)
Summary: The government taught him to kill. Now he's using his skills to help a woman seek revenge against the Miami underworld.. May Munro is a woman obsessed with getting revenge on the people who murdered her parents when she was still a girl. She hires Ray Quick, a retired explosives expert to kill her parent's killers. When Ned Trent, embittered ex-partner of Quick's is assigned to protect one of Quick's potential victims, a deadly game of cat and mouse ensues.
Genres: [Action, Thriller]
Cast: Sylvester Stallone as Ray Quick, Sharon Stone as May Munro, James Woods as Ned Trent, Rod Steiger as Joe Leon, Eric Roberts as Tomas Leon

ID: tt0110478
Title: Maverick (1994)
Summary: In their hands, a deck of cards was the only thing more dangerous than a gun.. Maverick is a gambler who would rather con someone than fight them. He needs an additional three thousand dollars in order to enter a Winner Take All poker game that begins in a few days. He tries to win some, tries to collect a few debts, and recover a little loot for the reward. He joins forces with a woman gambler with a marvelous southern accent as the two both try and enter the game.
Genres: [Action, Adventure, Comedy, Drama, Western]
Cast: Mel Gibson as Bret Maverick, Jodie Foster as Annabelle Bransford, Alfred Molina as Angel, James Coburn as Commodore Duvall, Dub Taylor as Room Clerk, Dan Hedaya as Twitchy, Riverboat Poker Player, Max Perlich as Johnny Hardin, Corey Feldman as Bank Robber, Steve Kahan as Dealer, Danny Glover as Bank Robber (uncredited)

ID: tt0111756
Title: Wyatt Earp (1994)
Summary: The epic story of love and adventure in a lawless land.. Covering the life and times of one of the West's most iconic heroes Wyatt Earp weaves an intricate tale of Earp and his friends and family. With a star studded cast, sweeping cinematography and authentic costumes Wyatt Earp led the way during the Western revival in the 90's.
Genres: [Drama, Action, Western]
Cast: Kevin Costner as Wyatt Earp, Dennis Quaid as Doc Holliday, Gene Hackman as Nicholas Earp, Michael Madsen as Virgil Earp, Catherine O'Hara as Allie Earp, Bill Pullman as Ed Masterson, Tom Sizemore as Bat Masterson, Rex Linn as Frank McLaury

ID: tt0113014
Title: Fall Time (1995)
Summary: Wrong People, Wrong Place, Wrong Time.. Three young men decide to plan a mock kidnapping, but everything goes wrong because a real bank robbery was already planned by two other guys.
Genres: [Action, Adventure, Crime, Drama, Thriller]
Cast: David Arquette as David, Stephen Baldwin as Leon

ID: tt0109198
Title: Bad Girls (1994)
Summary: It was a dangerous time to be a woman. And a good time to have friends.. Four former harlots try to leave the wild west (Colorado, to be exact) and head north to make a better life for themselves. Unfortunately someone from Cody's past won't let it happen that easily.
Genres: [Action, Adventure, Comedy, Drama, Western]
Cast: Madeleine Stowe as Cody Zamora, Mary Stuart Masterson as Anita Crown, Andie MacDowell as Eileen Spenser, Drew Barrymore as Lily Laronette, James Russo as Kid Jarrett, James Le Gros as William Tucker, Robert Loggia as Frank Jarrett, Dermot Mulroney as Josh McCoy

ID: tt0109439
Title: City Slickers II: The Legend of Curly's Gold (1994)
Summary: . On Mitch Robbins 40th birthday begins quite well until he returns home and finds his brother Glen, the black sheep of the family, in his sofa. Nevertheless he is about to have a wonderful birthday-night with his wife when he discovers a treasure map of Curly by chance. Together with Phil and unfortunately with Glen he tries to find the hidden gold of Curly's father in the desert of Arizona.
Genres: [Action, Comedy, Drama, Western]
Cast: Billy Crystal as Mitch Robbins, Jon Lovitz as Glen Robbins, Beth Grant as Lois, Bob Balaban as Dr. Jeffrey Sanborn, David Paymer as Ira Shalowitz, Josh Mostel as Barry Shalowitz

ID: tt0109493
Title: The Cowboy Way (1994)
Summary: . Two championship rodeo partners travel to New York to find their missing friend, Nacho Salazar who went missing there.
Genres: [Western, Action, Comedy]
Cast: Woody Harrelson as Pepper Lewis, Kiefer Sutherland as Sonny Gilstrap, Ernie Hudson as Officer Sam 'Mad Dog' Shaw, Luis Guzmán as Chango, Allison Janney as NYPD Computer Operator

ID: tt0106834
Title: Even Cowgirls Get the Blues (1994)
Summary: . Sissy Hankshaw (Uma Thurman) is born with enormous thumbs that help her hitchhiking through the US from a young age.  She becomes a model in advertising and her NY agent 'the Countess' (John Hurt) sends her to his ranch in CA to shoot a commercial, set against the background of mating whooping cranes.  There, she befriends Bonanza Jellybean (Rain Phoenix), one of the cowgirls at the beauty- ranch.
Genres: [Western, Drama, Comedy, Romance]
Cast: Uma Thurman as Sissy Hankshaw, Keanu Reeves as Julian Gitche, John Hurt as The Countess, Ed Begley Jr. as Rupert, Carol Kane as Carla, Sean Young as Marie Barth, Crispin Glover as Howard Barth, Heather Graham as Cowgirl Heather, Udo Kier as Commercial Director

ID: tt0107004
Title: Geronimo: An American Legend (1993)
Summary: A Warrior. A Leader. A Legend.. The Apache Indians have reluctantly agreed to settle on a US Government approved reservation. Not all the Apaches are able to adapt to the life of corn farmers. One in particular, Geronimo, is restless. Pushed over the edge by broken promises and necessary actions by the government, Geronimo and thirty or so other warriors form an attack team which humiliates the government by evading capture, whi
Genres: [History, Action, Drama, Western]
Cast: Gene Hackman as Brig. Gen. George Crook, Robert Duvall as Chief of Scouts Al Sieber, Matt Damon as 2nd Lt. Britton Davis, Mark Boone Junior as Afraid Miner, Scott Wilson as Redondo

ID: tt0110353
Title: Lightning Jack (1994)
Summary: . Lightning Jack Kane is an Australian outlaw in the wild west. During a bungled bank robbery he picks up mute Ben Doyle as a hostage. The two become good friends, with Jack teaching Ben how to rob banks, while they plan Jack's last heist.
Genres: [Action, Comedy, Western]
Cast: Beverly D'Angelo as Lana Castel, Cuba Gooding Jr. as Ben Doyle, Pat Hingle as U.S. Marshal Dan Kurtz, Richard Riehle as Marcus

ID: tt0083658
Title: Blade Runner (1982)
Summary: Man has made his match... now it's his problem.. In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.
Genres: [Science Fiction, Drama, Thriller]
Cast: Harrison Ford as Rick Deckard, Sean Young as Rachael, M. Emmet Walsh as Bryant, Daryl Hannah as Pris, Brion James as Leon Kowalski, James Hong as Hannibal Chew

ID: tt0108358
Title: Tombstone (1993)
Summary: Justice is coming.. Legendary marshal Wyatt Earp, now a weary gunfighter, joins his brothers Morgan and Virgil to pursue their collective fortune in the thriving mining town of Tombstone. But Earp is forced to don a badge again and get help from his notorious pal Doc Holliday when a gang of renegade brigands and rustlers begins terrorizing the town.
Genres: [Action, Adventure, Drama, History, Western]
Cast: Kurt Russell as Wyatt Earp, Val Kilmer as Doc Holliday, Bill Paxton as Morgan Earp, Charlton Heston as Henry Hooker, Michael Rooker as Sherman McMasters, Billy Bob Thornton as Johnny Tyler, Robert John Burke as Frank McLaury, Billy Zane as Mr. Fabian

ID: tt0099348
Title: Dances with Wolves (1990)
Summary: Inside everyone is a frontier waiting to be discovered.. Wounded Civil War soldier, John Dunbar tries to commit suicide – and becomes a hero instead. As a reward, he's assigned to his dream post, a remote junction on the Western frontier, and soon makes unlikely friends with the local Sioux tribe.
Genres: [Adventure, Drama, Western]
Cast: Kevin Costner as Lieutenant Dunbar, Mary McDonnell as Stands With A Fist, Maury Chaykin as Major Fambrough

ID: tt0065214
Title: The Wild Bunch (1969)
Summary: If you only want to spend two hours in a movie theatre and go home and forget it, stay away from THE WILD BUNCH.. Aging outlaw Pike Bishop (William Holden) prepares to retire after one final robbery. Joined by his gang, which includes Dutch Engstrom (Ernest Borgnine) and brothers Lyle (Warren Oates) and Tector Gorch (Ben Johnson), Bishop discovers the heist is a setup orchestrated in part by his old partner, Deke Thornton (Robert Ryan). As the remaining gang takes refuge in Mexican territory, Thornton trails them, resulting in fierce gunfights with plenty of casualties
Genres: [Adventure, Western]
Cast: Ernest Borgnine as Dutch Engstrom, Dub Taylor as Reverend Wainscoat

ID: tt0117517
Title: Roula (1995)
Summary: . Troubled Roula finds herself drawn to Leon, an author of children's books who has suffered an enormous emotional and creative blockage ever since his wife died in a motorcycle accident two years before. Leon has only his 12-year-old daughter Tanja to keep him company. As romance blooms between Leon and Roula, his daughter approves. Unfortunately, Roula is carrying heavy emotional baggage stemming from the incest she suffered at the hands of her father.
Genres: [Romance, Drama]
Cast: 

ID: tt0111613
Title: The Promise (1995)
Summary: A love, a hope, a wall.. East-Berlin, 1961, shortly after the erection of the Wall. Konrad, Sophie and three of their friends plan a daring escape to Western Germany. The attempt is successful, except for Konrad, who remains behind. From then on, and for the next 28 years, Konrad and Sophie will attempt to meet again, in spite of the Iron Curtain. Konrad, who has become a reputed Astrophysicist, tries to take advantage of scientific congresses outside Eastern Germany to arrange encounters with Sophie. But in a country where the political police, the Stasi, monitors the moves of all suspicious people (such as Konrad's sister Barbara and her husband Harald), preserving one's privacy, ideals and self-respect becomes an exhausting fight, even as the Eastern block begins its long process of disintegration.
Genres: [Drama, Romance]
Cast: 

ID: tt0112817
Title: Dead Man (1995)
Summary: No one can survive becoming a legend.. On the run after murdering a man, accountant William Blake encounters a strange North American man named Nobody who prepares him for his journey into the spiritual world.
Genres: [Drama, Fantasy, Western]
Cast: Johnny Depp as William Blake, Crispin Glover as Train Fireman, Lance Henriksen as Cole Wilson, Michael Wincott as Conway Twill, John Hurt as John Scholfield, Gabriel Byrne as Charlie Dickinson, Jared Harris as Benmont Tench, Alfred Molina as Trading Post Missionary, Billy Bob Thornton as Big George Drakoulious

ID: tt0109688
Title: Ashes of Time (1994)
Summary: . The film is set in five parts, five seasons that are part of the Chinese almanac. The story takes place in the jianghu, the world of the martial arts. Ouyang Feng has lived in the western desert for some years. He left his home in White Camel Mountain when the woman he loved chose to marry his elder brother rather than him. Instead of seeking glory, he ends up as an agent. When people come to him with a wish to eliminate someone who has wronged them, he puts them in touch with a swordsman who can do the job.
Genres: [Drama, Action]
Cast: 

ID: tt0114122
Title: Pharaoh's  Army (1995)
Summary: . Union soldiers in search of food descend on the farm of a Confederate family and decide to stay until one in their ranks' wounds have healed. While the war weary Union captain (Chris Cooper) falls for the mother of the family (Patricia Clarkson) - whose husband is off fighting for the rebels - her son plots revenge on the dirty, double-dealing Yankees. Co-stars Kris Kristofferson.
Genres: [Western, Drama, War]
Cast: Chris Cooper as Captain John Hull Abston, Kris Kristofferson as Preacher

ID: tt0116277
Title: The Fan (1996)
Summary: All fans have a favorite player.  This one has a favorite target.. When the San Francisco Giants pay centerfielder Bobby Rayburn $40 million to lead their team to the World Series, no one is happier or more supportive than #1 fan Gil Renard.  So when Rayburn becomes mired in the worst slump of his career, the obsessed Renard decides to stop at nothing to help his idol regain his former glory... not even murder.
Genres: [Drama, Mystery, Thriller]
Cast: Robert De Niro as Gil Renard, Wesley Snipes as Bobby Rayburn, Ellen Barkin as Jewel Stern, John Leguizamo as Manny, Benicio del Toro as Juan Primo, Frank Medrano as Leon, the Bartender

ID: tt0118073
Title: A Very Brady Sequel (1996)
Summary: The more everything changes the more they stay the same.. A man claiming to be Carol Brady's long-lost first husband, Roy Martin, shows up at the suburban Brady residence one evening. An impostor, the man is actually determined to steal the Bradys' familiar horse statue, a $20-million ancient Asian artifact.
Genres: [Comedy]
Cast: Rosie O'Donnell as Rosie O'Donnell, David Spade as Sergio, the hair stylist

ID: tt0104109
Title: Dingo (1991)
Summary: Truth is magic...It makes dreams come true.. Young John Anderson is captivated by jazz musician, Billy Cross when he performs on the remote airstrip of his Western Australian outback hometown after his plane is diverted. Years later, now a family man and making a meagre living tracking dingoes and playing trumpet in a local band, John still dreams of joining Billy on trumpet and makes a pilgrimage to Paris.
Genres: [Drama, Music]
Cast: 

ID: tt0032762
Title: The Mark of Zorro (1940)
Summary: . Around 1820 the son of a California nobleman comes home from Spain to find his native land under a villainous dictatorship. On the one hand he plays the useless fop, while on the other he is the masked avenger Zorro.
Genres: [Action, Adventure, Drama, Romance, Western]
Cast: Gino Corrado as Caballero (uncredited)

ID: tt0049261
Title: Giant (1956)
Summary: The legendary epic that's as big as Texas.. Sprawling epic covering the life of a Texas cattle rancher and his family and associates.
Genres: [Action, Drama, Western]
Cast: Dennis Hopper as Jordan "Jordy" Benedict III

ID: tt0039152
Title: Angel and the Badman (1947)
Summary: He lived only for revenge...She lived only for his love!. Notorious shootist and womanizer Quirt Evans' horse collapses as he passes a Quaker family's home. Quirt has been wounded, and the kindly family takes him in to nurse him back to health against the advice of others. The handsome Evans quickly attracts the affections of their beautiful daughter, Penelope. He develops an affection for the family, but his troubled past follows him.
Genres: [Romance, Western]
Cast: Bert Moorhouse as Gambler (uncredited), Pat Flaherty as Baker Brother (uncredited)

ID: tt0036241
Title: The Outlaw (1943)
Summary: Tall.... Terrific.... and Trouble!. Newly appointed sheriff Pat Garrett is pleased when his old friend Doc Holliday arrives in Lincoln, New Mexico on the stage. Doc is trailing his stolen horse, and it is discovered in the possession of Billy the Kid. In a surprising turnaround, Billy and Doc become friends. This causes the friendship between Doc and Pat to cool. The odd relationship between Doc and Billy grows stranger when Doc hides Billy at his girl, Rio's, place after Billy is shot.
Genres: [Action, Adventure, Western]
Cast: 

ID: tt0047977
Title: Davy Crockett, King of the Wild Frontier (1955)
Summary: NOW...on the MOTION PICTURE SCREEN!. Legends (and myths) from the life of famed American frontiersman Davey Crockett are depicted in this feature film edited from television episodes. Crockett and his friend George Russell fight in the Creek Indian War. Then Crockett is elected to Congress and brings his rough-hewn ways to the House of Representatives. Finally, Crockett and Russell journey to Texas and the last stand at the Alamo.
Genres: [Action, Adventure, Family, Western]
Cast: 

ID: tt0050798
Title: Old Yeller (1957)
Summary: All the heart, all the excitement of a great frontier adventure!. Young Travis Coates is left to take care of the family ranch with his mother and younger brother while his father goes off on a cattle drive in the 1860s. When a yellow mongrel comes for an uninvited stay with the family, Travis reluctantly adopts the dog.
Genres: [Adventure, Drama, Western]
Cast: 

ID: tt0114622
Title: North Star (1996)
Summary: Where land is power and revenge is obsession.. Set during the Alaskan gold rush of the late 1800's. In his efforts to gain control of a small mining town, Sean McLennon is buying up every mining claim that becomes available, usually after the deaths of the previous owners at the hands of McLennon's 'assistants'. One of the miners targeted by McLennon, a half-Indian hunter named Hudson Saanteek, manages to escape his hired thugs and comes back into town looking to re-establish his claim and get revenge. McLennon and his men have the advantange of numbers and weapons, but Saanteek has his survival skills and knowledge of the Alaskan wilderness.
Genres: [Action, Adventure, Drama, Western]
Cast: James Caan as Sean McLennon, Burt Young as Reno

ID: tt0120121
Title: The Shooter (1997)
Summary: . The story is of a small town in the early west and of a 'shooter' of reputation that drifts into it and stands up to the controlling family that runs it. But far from a John Wayne, this hero is caught and brutally beaten and left to die, only to be saved by a prostitute that has also suffered under the hand of this group of desperados. The only one possible to stand up to the shooter is another solitary man who joins with the notorious family although he is deputized as the town's sherif.
Genres: [Western]
Cast: 

ID: tt0060196
Title: The Good, the Bad and the Ugly (1966)
Summary: For three men the Civil War wasn't hell. It was practice.. While the Civil War rages between the Union and the Confederacy, three men – a quiet loner, a ruthless hit man and a Mexican bandit – comb the American Southwest in search of a strongbox containing $200,000 in stolen gold.
Genres: [Western]
Cast: Clint Eastwood as Blondie, Philip Ettington as Pedro (uncredited)

ID: tt0064116
Title: Once Upon a Time in the West (1968)
Summary: There were three men in her life. One to take her... one to love her... and one to kill her.. This classic western masterpiece is an epic film about a widow whose land and life are in danger as the railroad is getting closer and closer to taking them over. A mysterious harmonica player joins forces with a desperado to protect the woman and her land.
Genres: [Western]
Cast: Henry Fonda as Frank, Jason Robards as Cheyenne, Keenan Wynn as Sheriff

ID: tt0041959
Title: The Third Man (1949)
Summary: Hunted by men ... Sought by WOMEN!. Set in postwar Vienna, Austria, "The Third Man" stars Joseph Cotten as Holly Martins, a writer of pulp Westerns, who arrives penniless as a guest of his childhood chum Harry Lime, only to find him dead. Martins develops a conspiracy theory after learning of a "third man" present at the time of Harry's death, running into interference from British officer Maj. Calloway and falling head-over-heels for Harry's grief-stricken lover, Anna.
Genres: [Thriller, Mystery]
Cast: Joseph Cotten as Holly Martins

ID: tt0087843
Title: Once Upon a Time in America (1984)
Summary: Crime, passion and lust for power - Sergio Leone's explosive saga of gangland America.. A former Prohibition-era Jewish gangster returns to the Lower East Side of Manhattan over thirty years later, where he once again must confront the ghosts and regrets of his old life.
Genres: [Drama, Crime]
Cast: Robert De Niro as David 'Noodles' Aaronson, James Woods as Maximilian 'Max' Bercovicz, Elizabeth McGovern as Deborah Gelly, Joe Pesci as Frankie Minaldi, Burt Young as Joe, Danny Aiello as Police Chief Vincent Aiello, William Forsythe as Philip 'Cockeye' Stein, James Russo as Bugsy, Louise Fletcher as Cemetery Director (uncredited)

ID: tt0040897
Title: The Treasure of the Sierra Madre (1948)
Summary: The nearer they get to their treasure, the farther they get from the law.. Fred C. Dobbs and Bob Curtin, both down on their luck in Tampico, Mexico in 1925, meet up with a grizzled prospector named Howard and decide to join with him in search of gold in the wilds of central Mexico. Through enormous difficulties, they eventually succeed in finding gold, but bandits, the elements, and most especially greed threaten to turn their success into disaster.
Genres: [Action, Adventure, Drama, Western]
Cast: Pat Flaherty as Customer in Bar Who Warns Curtin and Dobbs about Pat McCormick (uncredited)

ID: tt0105695
Title: Unforgiven (1992)
Summary: Some legends will never be forgotten. Some wrongs can never be forgiven.. William Munny is a retired, once-ruthless killer turned gentle widower and hog farmer. To help support his two motherless children, he accepts one last bounty-hunter mission to find the men who brutalized a prostitute. Joined by his former partner and a cocky greenhorn, he takes on a corrupt sheriff.
Genres: [Western]
Cast: Clint Eastwood as Bill Munny, Gene Hackman as Little Bill Daggett, Morgan Freeman as Ned Logan, Anna Levine as Delilah Fitzgerald, Philip Ettington as Hank (uncredited)

ID: tt0044706
Title: High Noon (1952)
Summary: The story of a man who was too proud to run.. High Noon is about a recently freed leader of a gang of bandits in the desert who is looking to get revenge on the Sheriff who put him in jail. A legendary western film from the Austrian director Fred Zinnemann.
Genres: [Western]
Cast: Lon Chaney Jr. as Martin Howe

ID: tt0064115
Title: Butch Cassidy and the Sundance Kid (1969)
Summary: Not that it matters, but most of it is true.. In late 1890s Wyoming, Butch Cassidy is the affable, clever and talkative leader of the outlaw Hole in the Wall Gang. His closest companion is the laconic dead-shot 'Sundance Kid'. As the west rapidly becomes civilized, the law finally catches up to Butch, Sundance and their gang.  Chased doggedly by a special posse, the two decide to make their way to South America in hopes of evading their pursuers once and for all.
Genres: [History, Drama, Western, Crime]
Cast: Paul Newman as Butch Cassidy, Robert Redford as Sundance Kid, Cloris Leachman as Agnes, Kenneth Mars as Marshal

ID: tt0096487
Title: Young Guns (1988)
Summary: Six reasons why the west was wild.. A group of young gunmen, led by Billy the Kid, become deputies to avenge the murder of the rancher who became their benefactor. But when Billy takes their authority too far, they become the hunted.
Genres: [Crime, Action, Adventure, Drama, Western]
Cast: Emilio Estevez as William H. 'Billy the Kid' Bonney, Kiefer Sutherland as Josiah Gordon 'Doc' Scurlock, Charlie Sheen as Dick Brewer, Dermot Mulroney as Dirty Steve Stephens, Terence Stamp as John Tunstall, Tom Cruise as [Cameo]

ID: tt0100994
Title: Young Guns II (1990)
Summary: Yoo-Hoo, I'll make ya famous!. Only three of the original five "young guns" -- Billy the Kid (Emilio Estevez), Jose Chavez y Chavez (Lou Diamond Phillips), and Doc Scurlock (Kiefer Sutherland) -- return in Young Guns, Part 2, which is the story of Billy the Kid and his race to safety in Old Mexico while being trailed by a group of government agents led by Pat Garrett
Genres: [Western, Adventure]
Cast: Emilio Estevez as William H. 'Billy the Kid' Bonney, Kiefer Sutherland as Josiah Gordon 'Doc' Scurlock, Christian Slater as Arkansas Dave Rudabaugh, Viggo Mortensen as John W. Poe, Tracey Walter as Beever Smith, Bradley Whitford as Charles Phalen, Scott Wilson as Governor Lewis Wallace, James Coburn as John Simpson Chisum

ID: tt0052287
Title: Terror in a Texas Town (1958)
Summary: When the Texas Plains Ran With Blood and Black Gold!. Armed with a harpoon, a Swedish whaler is out for revenge after the death of his father. A greedy oil man trying to buy up the Swede's land might be the guilty party.
Genres: [Action, Western]
Cast: 

ID: tt0120152
Title: Smilla's Sense of Snow (1997)
Summary: Some Tracks Should Never Be Uncovered.. Smilla Jaspersen, half Danish, half Greenlander, attempts to understand the death of a small boy who falls from the roof of her apartment building. Suspecting wrongdoing, Smilla uncovers a trail of clues leading towards a secretive corporation that has made several mysterious expeditions to Greenland. Scenes from the film were shot in Copenhagen and western Greenland. The film was entered into the 47th Berlin International Film Festival, where director Bille August was nominated for the Golden Bear.
Genres: [Action, Crime, Drama, Mystery, Thriller]
Cast: Gabriel Byrne as The Mechanic, Jim Broadbent as Dr. Lagermann, Tom Wilkinson as Prof. Loyen, Robert Loggia as Moritz Jasperson, Vanessa Redgrave as Elsa Lübing

ID: tt0145302
Title: A Paralyzing Fear: The Story of Polio in America (1998)
Summary: . This is a very clear and personalized presentation of the history and human impact of a seriously debilitating disease - one which we in the western world are too often tempted to think of in the past tense.
Genres: [Documentary]
Cast: 

ID: tt0119053
Title: Almost Heroes (1998)
Summary: Almost History... Almost Legends... Mostly Ridiculous.. Two hapless explorers lead an ill-fated 1804 expedition through the Pacific Northwest in a hopeless, doomed effort to reach the Pacific Ocean before Lewis and Clark.
Genres: [Action, Adventure, Comedy, Western]
Cast: Bokeem Woodbine as Jonah, Eugene Levy as Guy Fontenot, Kevin Dunn as Hidalgo, Harry Shearer as Narrator (voice)

ID: tt0020629
Title: All Quiet on the Western Front (1930)
Summary: They left for war as boys never to return as men.. A young soldier faces profound disillusionment in the soul-destroying horror of World War I. Together with several other young German soldiers, he experiences the horrors of war, such evil of which he had not conceived of when signing up to fight. They eventually become sad, tormented, and confused of their purpose.
Genres: [Drama, War]
Cast: 

ID: tt0021746
Title: Cimarron (1931)
Summary: Terrific as all creation!. When the government opens up the Oklahoma territory for settlement, restless Yancey Cravat claims a plot of the free land for himself and moves his family there from Wichita. A newspaperman, lawyer, and just about everything else, Cravat soon becomes a leading citizen of the boom town of Osage. Once the town is established, however, he begins to feel confined once again, and heads for the Cherokee Strip, leaving his family behind. During this and other absences, his wife Sabra must learn to take care of herself and soon becomes prominent in her own right.
Genres: [Drama, Western]
Cast: 

ID: tt0078790
Title: The Apple Dumpling Gang Rides Again (1979)
Summary: As belles, they were ding-dongs... they enlisted, and were rotten to the corps... and when things got hotter... they went to the cooler... now, they're on the loose again!. Amos and Theodore, the two bumbling outlaw wannabes from The Apple Dumpling Gang, are back and trying to make it on their own. This time, the crazy duo gets involved in an army supply theft case -- and, of course, gets in lots of comic trouble along the way!
Genres: [Comedy, Family, Western]
Cast: Kenneth Mars as Marshal Woolly Bill Hitchcock, Art Evans as Baggage Master

ID: tt0077698
Title: Hot Lead & Cold Feet (1978)
Summary: . Twin brothers -- one rough and tough, the other a city-bred milquetoast -- compete for their father's fortune.
Genres: [Action, Comedy, Family, Western]
Cast: 

ID: tt0111359
Title: Tall Tale (1995)
Summary: Sometime the hero ends up being you!. A young boy draws on the inspiration of legendary western characters to find the strength to fight an evil land baron in the old west who wants to steal his family's farm and destroy their idyllic community. When Daniel Hackett sees his father Jonas gravely wounded by the villainous Stiles, his first urge is for his family to flee the danger, and give up their life on a farm which Daniel has come to despise anyway. Going alone to a lake to try to decide what to do, he falls asleep on a boat and wakes to find himself in the wild west, in the company of such "tall tale" legends as Pecos Bill, Paul Bunyan, John Henry and Calamity Jane. Together, they battle the same villains Daniel is facing in his "real" world, ending with a heroic confrontation in which the boy stands up to Stiles and his henchmen, and rallies his neighbors to fight back against land grabbers who want to destroy their town.
Genres: [Adventure]
Cast: Patrick Swayze as Pecos Bill, Oliver Platt as Paul Bunyan, Scott Glenn as J.P. Stiles, Jared Harris as Head Thug Pug, Catherine O'Hara as Calamity Jane

ID: tt0080801
Title: The Gods Must Be Crazy (1980)
Summary: The critics are raving... the natives are restless... and the laughter is non-stop!. Misery is brought to a small group of Sho in the Kalahari desert in the form of a cola bottle. In his quest to throw the evil object over the edge of the earth, Xixo encounters Western "civilization," a haphazard doctor and a tyranical despot.
Genres: [Action, Comedy]
Cast: 

ID: tt0120775
Title: One Man's Hero (1999)
Summary: One man's hero is another man's traitor.. One Man's Hero tells the little-known story of the "St. Patrick's Battalion" or "San Patricios," a group of mostly Irish and other immigrants of the Catholic faith who deserted to Mexico after encountering religious and ethnic prejudice in the U.S. Army during the Mexican-American War. The plot centers around the personal story of John Riley, an Irishman who had been a sergeant in the American Army who is commissioned as a captain in the Mexican army and commands the battalion, as he leads his men in battle and struggles with authorities on both sides of the border
Genres: [Western, Action, Drama, History]
Cast: Stephen Tobolowsky as Captain Gaine

ID: tt0116488
Title: Hard Core Logo (1996)
Summary: . Bruce Macdonald follows punk bank Hard Core Logo on a harrowing last-gasp reunion tour throughout Western Canada. As magnetic lead-singer Joe Dick holds the whole magilla together through sheer force of will, all the tensions and pitfalls of life on the road come bubbling to the surface.
Genres: [Drama, Comedy, Music]
Cast: 

ID: tt0091159
Title: Gung Ho (1986)
Summary: When East meets West, the laughs shift into high gear!. When a western Pennsylvania auto plant is acquired by a Japanese company, brokering auto worker Hunt Stevenson faces the tricky challenge of mediating the assimilation of two clashing corporate cultures. At one end is the Japanese plant manager and the sycophant who is angling for his position. At the other, a number of disgruntled long-time union members struggle with the new exigencies of Japanese quality control.
Genres: [Comedy, Drama]
Cast: Michael Keaton as Hunt Stevenson, George Wendt as Buster, Mimi Rogers as Audrey, John Turturro as Willie, Clint Howard as Paul, Rance Howard as Mayor Conrad Zwart

ID: tt0089767
Title: Pale Rider (1985)
Summary: ... and hell followed with him.. A small gold mining camp is terrorised by a ruthless land owner wanting to take their land. Clint Eastwood arrives riding a pale horse just as a young girl is praying to God to help the miners. He is revealed to be a preacher with mysterious and possible otherworldly origins who teams up with the miners to defeat the land owner and the corrupt sheriff.
Genres: [Romance, Western]
Cast: Clint Eastwood as Preacher, Chris Penn as Josh LaHood

ID: tt0120699
Title: The Hi-Lo Country (1998)
Summary: A woman like Mona can drive men to extremes.. An intimate story of the enduring bond of friendship between two hard-living men, set against a sweeping backdrop: the American West, post-World War II, in its twilight. Pete and Big Boy are masters of the prairie, but ultimately face trickier terrain: the human heart.
Genres: [Drama, Action, Western, Romance]
Cast: Woody Harrelson as Big Boy Matson, Patricia Arquette as Mona, John Diehl as Les Birk

ID: tt0092086
Title: ¡Three Amigos! (1986)
Summary: They're Down On Their Luck And Up To Their Necks In Senoritas, Margaritas, Banditos And Bullets!. Three unemployed actors accept an invitation to a Mexican village to replay their bandit fighter roles, unaware that it is the real thing.
Genres: [Comedy, Western]
Cast: Chevy Chase as Dusty Bottoms, Steve Martin as Lucky Day, Jon Lovitz as Morty, Joe Mantegna as Harry Flugleman, Phil Hartman as Sam

ID: tt0070909
Title: Westworld (1973)
Summary: Westworld ...where robot men and women are programmed to serve you for ...Romance ...Violence ...Anything. In a futuristic resort, wealthy patrons can visit recreations of different time periods and experience their wildest fantasies with life-like robots. But when Richard Benjamin opts for the wild west, he gets more than he bargained for when a gunslinger robot goes berserk.
Genres: [Action, Adventure, Drama, Horror, Science Fiction, Thriller, Western]
Cast: 

ID: tt0129332
Title: Ravenous (1999)
Summary: You Are Who You Eat. Upon receiving reports of missing persons at Fort Spencer, a remote Army outpost on the Western frontier, Capt. John Boyd investigates. After arriving at his new post, Boyd and his regiment aid a wounded frontiersman who recounts a horrifying tale of a wagon train murdered by its supposed guide -- a vicious U.S. Army colonel gone rogue. Fearing the worst, the regiment heads out into the wilderness to verify the gruesome claims
Genres: [Comedy, Horror, Thriller, Western]
Cast: David Arquette as Private Cleaves, Jeffrey Jones as Col. Hart

ID: tt0120891
Title: Wild Wild West (1999)
Summary: It's a whole new west.. Legless Southern inventor Dr. Arliss Loveless plans to rekindle the Civil War by assassinating President U.S. Grant. Only two men can stop him: gunfighter James West and master-of-disguise and inventor Artemus Gordon. The two must team up to thwart Loveless' plans.
Genres: [Action, Adventure, Comedy, Science Fiction, Western]
Cast: Kevin Kline as U.S. Marshal Artemus Gordon, Kenneth Branagh as Dr. Arliss Loveless, Salma Hayek as Rita Escobar, M. Emmet Walsh as Coleman, Ian Abercrombie as British Dignitary

ID: tt0060095
Title: Alvarez Kelly (1966)
Summary: Carving a legend of greatness from the Blue Ridge to the Rio Grande !. A suave Mexican cattleman inadvertently gets involved in the Civil War.
Genres: [Action, War, Western]
Cast: 

ID: tt0068699
Title: High Plains Drifter (1973)
Summary: Welcome to Hell. A gunfighting stranger comes to the small settlement of Lago. After gunning down three gunmen who tried to kill him, the townsfolk decide to hire the Stranger to hold off three outlaws who are on their way.
Genres: [Western]
Cast: Clint Eastwood as The Stranger, Mitchell Ryan as Dave Drake

ID: tt0061747
Title: Hang 'em High (1968)
Summary: The hanging was the best show in town. But they made two mistakes. They hung the wrong man and they didn't finish the job!. Marshall Jed Cooper survives a hanging, vowing revenge on the lynch mob that left him dangling. To carry out his oath for vengeance, he returns to his former job as a lawman. Before long, he's caught up with the nine men on his hit list and starts dispensing his own brand of Wild West justice.
Genres: [Western]
Cast: Clint Eastwood as Marshal Jed Cooper, Pat Hingle as Judge Adam Fenton, Bruce Dern as Miller, Dennis Hopper as Prophet

ID: tt0058461
Title: A Fistful of Dollars (1964)
Summary: In his own way he is perhaps, the most dangerous man who ever lived!. The Man With No Name enters the Mexican village of San Miguel in the midst of a power struggle among the three Rojo brothers and sheriff John Baxter. When a regiment of Mexican soldiers bearing gold intended to pay for new weapons is waylaid by the Rojo brothers, the stranger inserts himself into the middle of the long-simmering battle, selling false information to both sides for his own benefit.
Genres: [Western]
Cast: Clint Eastwood as Joe

ID: tt0062218
Title: Rough Night in Jericho (1967)
Summary: A former deputy and a strong-willed widow determine to stop a ruthless town boss.. The only business in the Wild West town of Jericho that corrupt sheriff Alex Flood doesn't control behind the scenes is the stagecoach owned by tough-willed widow Molly Lang and her right-hand man, Hickman. Former marshal Dolan, recently hired by Lang and Hickman as a driver, wants to stay out of the mess, but when he sees Flood's henchman Yarbrough assault Lang, he steps up to fight the corruption.
Genres: [Western]
Cast: 

ID: tt0065988
Title: Little Big Man (1970)
Summary: Either The Most Neglected Hero In History Or A Liar Of Insane Proportion!. Jack Crabb, looking back from extreme old age, tells of his life being raised by Indians and fighting with General Custer.
Genres: [Adventure, Western, History]
Cast: Dustin Hoffman as Jack Crabb, Faye Dunaway as Mrs. Louise Pendrake, William Hickey as Historian, M. Emmet Walsh as Shotgun Guard

ID: tt0068762
Title: Jeremiah Johnson (1972)
Summary: His Mountain. His Peace. His Great Hunts. His Young Bride. With All That, It Should Have Been Different.. A mountain man who wishes to live the life of a hermit becomes the unwilling object of a long vendetta by Indians when he proves to be the match of their warriors in one-to-one combat on the early frontier.
Genres: [Adventure, Western]
Cast: Robert Redford as Jeremiah Johnson

ID: tt0067411
Title: McCabe & Mrs. Miller (1971)
Summary: Name your poison. A gambler and a prostitute become business partners in a remote Old West mining town, and their enterprise thrives until a large corporation arrives on the scene.
Genres: [Western]
Cast: Warren Beatty as John McCabe, Shelley Duvall as Ida Coyle

ID: tt0134154
Title: Ride with the Devil (1999)
Summary: In a No-man's Land between North and South, You didn't fight for the Blue or the Grey... You fought for your friends and family.. Ride with the Devil follows four people who are fighting for truth and justice amidst the turmoil of the American Civil War. Director Ang Lee takes us to a no man's land on the Missouri/Kansas border where a staunch loyalist, an immigrant's son, a freed slave, and a young widow form an unlikely friendship as they learn how to survive in an uncertain time. In a place without rules and redefine the meaning of bravery and honor.
Genres: [Drama, War, Romance, Western]
Cast: Skeet Ulrich as Jack Bull Chiles, Tom Wilkinson as Orton Brown

ID: tt0033021
Title: Santa Fe Trail (1940)
Summary: Where the railroad and civilization ended, the Sante Fe Trail began!. As a penalty for fighting fellow classmates days before graduating from West Point, J.E.B. Stuart, George Armstrong Custer and four friends are assigned to the 2nd Cavalry, stationed at Fort Leavenworth. While there they aid in the capture and execution of the abolitionist, John Brown following the Battle of Harper's Ferry.
Genres: [Action, Western]
Cast: 

- Output of chatgpt:
- - **Query:** *sergio leone western*
This clearly refers to **Western films directed by Sergio Leone**.

### ✅ Relevant results

These are Westerns **directed by Sergio Leone** and directly match the query:

* **tt0058461 — A Fistful of Dollars (1964)**
* **tt0060196 — The Good, the Bad and the Ugly (1966)**
* **tt0064116 — Once Upon a Time in the West (1968)**

### ❌ Not relevant

All other results are **not relevant** because they are either:

* Not Westerns, or
* Westerns **not directed by Sergio Leone**, or
* Directed by Sergio Leone but **not Westerns** (e.g., *Once Upon a Time in America*).

### ⚠️ Not relevant but closely related

* **tt0087843 — Once Upon a Time in America (1984)**
  Directed by Sergio Leone, but it is a **crime drama**, not a Western.

### ✅ Final answer

Only **three movies** in the results are relevant to the query **“sergio leone western”**:

* *A Fistful of Dollars*
* *The Good, the Bad and the Ugly*
* *Once Upon a Time in the West*

## Task01.a. Query03:
- query = "Movies like 'The good the bad and the ugly'"
- Notes: keeping the code as after query02
- Notebook ouput with query:
- - 
You are assessing movies. Given the query below and the results, assess which movies are relevant for the query

Query: Movies like 'The good the bad and the ugly'

Results:
ID: tt0114885
Title: Waiting to Exhale (1995)
Summary: Friends are the people who let you be yourself... and never let you forget it.. Cheated on, mistreated and stepped on, the women are holding their breath, waiting for the elusive "good man" to break a string of less-than-stellar lovers. Friends and confidants Vannah, Bernie, Glo and Robin talk it all out, determined to find a better way to breathe.
Genres: [Comedy, Drama, Romance]
Cast: Angela Bassett as Bernadine 'Bernie' Harris, Mykelti Williamson as Troy, Wesley Snipes as James Wheeler

ID: tt0113041
Title: Father of the Bride Part II (1995)
Summary: Just When His World Is Back To Normal... He's In For The Surprise Of His Life!. Just when George Banks has recovered from his daughter's wedding, he receives the news that she's pregnant ... and that George's wife, Nina, is expecting too. He was planning on selling their home, but that's a plan that -- like George -- will have to change with the arrival of both a grandchild and a kid of his own.
Genres: [Comedy]
Cast: Steve Martin as George Banks, Diane Keaton as Nina Banks, Eugene Levy as Mr. Habib

ID: tt0114319
Title: Sabrina (1995)
Summary: You are cordially invited to the most surprising merger of the year.. An ugly duckling having undergone a remarkable change, still harbors feelings for her crush: a carefree playboy, but not before his business-focused brother has something to say about it.
Genres: [Comedy, Romance]
Cast: Harrison Ford as Linus Larrabee, John Wood as Fairchild, Dana Ivey as Mack, Paul Giamatti as Scott, Margo Martindale as Nurse

ID: tt0112302
Title: Tom and Huck (1995)
Summary: The Original Bad Boys.. A mischievous young boy, Tom Sawyer, witnesses a murder by the deadly Injun Joe. Tom becomes friends with Huckleberry Finn, a boy with no future and no family. Tom has to choose between honoring a friendship or honoring an oath because the town alcoholic is accused of the murder. Tom and Huck go through several adventures trying to retrieve evidence.
Genres: [Action, Adventure, Drama, Family]
Cast: 

ID: tt0113161
Title: Get Shorty (1995)
Summary: The mob is tough, but it’s nothing like show business.. Chili Palmer is a Miami mobster who gets sent by his boss, the psychopathic "Bones" Barboni, to collect a bad debt from Harry Zimm, a Hollywood producer who specializes in cheesy horror films. When Chili meets Harry's leading lady, the romantic sparks fly. After pitching his own life story as a movie idea, Chili learns that being a mobster and being a Hollywood producer really aren't all that different.
Genres: [Comedy, Thriller, Crime]
Cast: John Travolta as Chili Palmer, Gene Hackman as Harry Zimm, Rene Russo as Karen Flores, Danny DeVito as Martin Weir, Dennis Farina as Ray 'Bones' Barboni, Delroy Lindo as Bo Catlett, James Gandolfini as Bear, David Paymer as Leo Devoe

ID: tt0112431
Title: Babe (1995)
Summary: A little pig goes a long way.. Babe is a little pig who doesn't quite know his place in the world. With a bunch of odd friends, like Ferdinand the duck who thinks he is a rooster and Fly the dog he calls mom, Babe realizes that he has the makings to become the greatest sheep pig of all time, and Farmer Hogget knows it. With the help of the sheep dogs Babe learns that a pig can be anything that he wants to be.
Genres: [Fantasy, Drama, Comedy, Family]
Cast: Miriam Margolyes as Fly the Female Sheepdog (voice), James Cromwell as Farmer Arthur Hoggett, Roscoe Lee Browne as Narrator (voice)

ID: tt0113855
Title: Mortal Kombat (1995)
Summary: Nothing In This World Has Prepared You For This.. For nine generations an evil sorcerer has been victorious in hand-to-hand battle against his mortal enemies. If he wins a tenth Mortal Kombat tournament, desolation and evil will reign over the multiverse forever. To save Earth, three warriors must overcome seemingly insurmountable odds, their own inner demons, and superhuman foes in this action/adventure movie based on one of the most popular video games of all time.
Genres: [Action, Fantasy]
Cast: Bridgette Wilson as Sonya Blade

ID: tt0116126
Title: Don't Be a Menace to South Central While Drinking Your Juice in the Hood (1996)
Summary: Finally, the movie that proves that Justice isn't always Poetic, Jungle Fever isn't always pretty, and Higher Learning can be a waste of time.. When Ashtray (Shawn Wayans) moves to South Central L.A. to live with his father (who appears to be the same age he is) and grandmother (who likes to talk tough and smoke reefer), he falls in with his gang-banging cousin Loc Dog (Marlon Wayans), who along with the requisite pistols and Uzi carries a thermo-nuclear warhead for self-defense. Will Ashtray be able to keep living the straight life?
Genres: [Comedy]
Cast: Vivica A. Fox as Ashtray's Mother, Bernie Mac as Officer Self Hatred

ID: tt0114753
Title: Two Bits (1995)
Summary: You're never too old to believe in a dream. Or too young to make one come true!. It's a hot summer day in 1933 in South Philly, where 12-year old Gennaro lives with his widowed mom and his ailing grandpa, who sits outside holding tight to his last quarter, which he's promised to Gennaro and which Gennaro would like to have to buy a ticket to the plush new movie theater. But grandpa's not ready to pass on the quarter or pass on to his final reward: he has some unfinished business with a woman from his past, and he enlists Gennaro to act as his emissary.
Genres: [Drama, Family]
Cast: Al Pacino as Gitano Sabatoni, Joe Grifasi as Uncle Joe

ID: tt0113537
Title: Kicking and Screaming (1995)
Summary: Anxiety loves company.. After college graduation, Grover's girlfriend Jane tells him she's moving to Prague to study writing. Grover declines to accompany her, deciding instead to move in with several friends, all of whom can't quite work up the inertia to escape their university's pull. Nobody wants to make any big decisions that would radically alter his life, yet none of them wants to end up like Chet, the professional student who tends bar and is in his tenth year of university studies.
Genres: [Comedy, Drama, Romance]
Cast: Eric Stoltz as Chet, Parker Posey as Miami, Elliott Gould as Grover's Dad

ID: tt0116731
Title: The Juror (1996)
Summary: There is no defense.. With his gangster boss on trial for murder, a mob thug known as "the Teacher" tells Annie Laird she must talk her fellow jurors into a not-guilty verdict, implying that he'll kill her son Oliver if she fails. She manages to do this, but, when it becomes clear that the mobsters might want to silence her for good, she sends Oliver abroad and tries to gather evidence of the plot against her, setting up a final showdown.
Genres: [Drama, Thriller]
Cast: Demi Moore as Annie Laird, Alec Baldwin as Teacher, Anne Heche as Juliet, James Gandolfini as Eddie

ID: tt0114039
Title: Once Upon a Time... When We Were Colored (1996)
Summary: . This film relates the story of a tightly connected Afro-American community informally called Colored Town where the inhabitants live and depend on each other in a world where racist oppression is everywhere, as told by a boy called Cliff who spent his childhood there. Despite this, we see the life of the community in all its joys and sorrows, of those that live there while others decide to leave for a better life north. For those remaining, things come to a serious situation when one prominent businessman is being muscled out by a white competitor using racist intimidation. In response, the community must make the decision of whether to submit meekly like they always have, or finally fight for their rights.
Genres: [Romance, Drama]
Cast: 

ID: tt0113490
Title: The Journey of August King (1995)
Summary: . The Journey of August King is a multi-dimensional drama about a North Carolina farmer in 1815. August King, a widower, is on his way home as he does every year after selling his produce and purchasing the stock and goods he will need to survive the winter. On his journey, he comes upon a run-away slave, a young woman about 19 and August King must decide to violate the law and help this slave to freedom or else leave her to be hunted down and, ultimately, returned to her slave owner.
Genres: [Drama]
Cast: 

ID: tt0115639
Title: Beautiful Girls (1996)
Summary: Good times never seemed so good,. During a snowy winter in the small fictional town of Knight"s Ridge, Massachusetts, a group of lifelong buddies hang out, drink and struggle to connect with the women who affect their decisions, dreams and desires.
Genres: [Comedy, Drama, Romance]
Cast: Matt Dillon as Tommy 'Birdman' Rowland, Uma Thurman as Andera, Mira Sorvino as Sharon Cassidy, Michael Rapaport as Paul Kirkwood, Rosie O'Donnell as Gina Barrisano, Max Perlich as Kev, Martha Plimpton as Jan, David Arquette as Bobby Conway, John Carroll Lynch as Frank Womack

ID: tt0113283
Title: Heidi Fleiss: Hollywood Madam (1995)
Summary:    . A documentary crew from the BBC arrives in L.A. intent on interviewing Heidi Fleiss, a year after her arrest for running a brothel but before her trial. Several months elapse before the interview, so the crew searches for anyone who'll talk about the young woman. Two people have a lot to say to the camera: a retired madam named Alex for whom Fleiss once worked and Fleiss's one-time boyfriend, Ivan Nagy, who introduced her to Alex. Alex and Nagy don't like each other, so the crew shuttles between them with "she said" and "he said." When they finally interview Fleiss, they spend their time reciting what Alex and Nagy have had to say and asking her reaction.
Genres: [Documentary]
Cast: 

ID: tt0112556
Title: Happiness Is in the Field (1995)
Summary: . Francis, the boss of a small plumbing supply company, is harassed by tax collectors, striking employees, and an impossible wife and daughter. His only joy is sharing lunch with his friend Gerard. Then a TV show called "where are you?" shows a woman from Gers who is searching for her husband who disappeared 28 years ago. The lost husband looks like an identical twin of Francis...
Genres: [Comedy]
Cast: 

ID: tt0112373
Title: Anne Frank Remembered (1995)
Summary: She is perhaps Hitler's best known victim, but what was Anne Frank really like?. Using previously unreleased archival material in addition to contemporary interviews, this academy award-winning documentary tells the story of the Frank family and presents the first fully-rounded portrait of their brash and free-spirited daughter Anne, perhaps the world's most famous victim of the Holocaust. Written by Dawn M. Barclift
Genres: [Documentary, Drama, Foreign]
Cast: Kenneth Branagh as Narration (voice), Glenn Close as Diary Readings (voice)

ID: tt0112442
Title: Bad Boys (1995)
Summary: Whatcha gonna do?. Marcus Burnett is a hen-pecked family man. Mike Lowry is a foot-loose and fancy free ladies' man. Both are Miami policemen, and both have 72 hours to reclaim a consignment of drugs stolen from under their station's nose. To complicate matters, in order to get the assistance of the sole witness to a murder, they have to pretend to be each other.
Genres: [Action, Comedy, Crime, Thriller]
Cast: Tchéky Karyo as Fouchet, Joe Pantoliano as Captain C. Howard, Michael Imperioli as Jojo, Anna Levine as Francine, Kevin Corrigan as Elliot

ID: tt0112541
Title: Blue in the Face (1995)
Summary: Welcome to the planet Brooklyn.. Auggie runs a small tobacco shop in Brooklyn, New York. The whole neighborhood comes to visit him to buy cigarettes and have some small talk. During the movie Lou Reed tries to explain why he has to have a cut on his health insurance bill if he keeps smoking and Madonna acts as a Singing Telegram.
Genres: [Comedy]
Cast: Harvey Keitel as Auggie Wren, Michael J. Fox as Pete Maloney, Lily Tomlin as Waffle eater, Giancarlo Esposito as Tommy Finelli, Mira Sorvino as Young lady, Keith David as Jackie Robinson, Victor Argo as Vinnie, Jared Harris as Jimmy Rose

ID: tt0109508
Title: Crumb (1994)
Summary: Weird sex · Obsession · Comic books. This movie chronicles the life and times of R. Crumb. Robert Crumb is the cartoonist/artist who drew Keep On Truckin', Fritz the Cat, and played a major pioneering role in the genesis of underground comix. Through interviews with his mother, two brothers, wife, and ex-girlfriends, as well as selections from his vast quantity of graphic art, we are treated to a darkly comic ride through one man's subconscious mind.
Genres: [Documentary]
Cast: 

ID: tt0113677
Title: Living in Oblivion (1995)
Summary: Nick is about to discover the first rule of filmmaking: if at first you don't succeed... PANIC!. Film about filmmaking. It takes place during one day on set of non-budget movie. Ultimate tribute to all independent filmmakers.
Genres: [Drama, Comedy]
Cast: Steve Buscemi as Nick Reve, Dermot Mulroney as Wolf, James Le Gros as Chad Palomino, Kevin Corrigan as Assistant Camera

ID: tt0113820
Title: Mighty Morphin Power Rangers: The Movie (1995)
Summary: The Power Is On!. Power up with six incredible teens who out-maneuver and defeat evil everywhere as the Mighty Morphin Power Ranger, But this time the Power Rangers may have met their match, when they face off with the most sinister monster the galaxy has ever seen.
Genres: [Action, Adventure, Science Fiction, Family, Fantasy]
Cast: 

ID: tt0110620
Title: Nadja (1994)
Summary: Unseen. Unforgiving. Undead.. This ultra-hip, post-modern vampire tale is set in contemporary New York City. Members of a dysfunctional family of vampires are trying to come to terms with each other, in the wake of their father's death. Meanwhile, they are being hunted by Dr. Van Helsing and his hapless nephew. As in all good vampire movies, forces of love are pitted against forces of destruction.
Genres: [Horror, Thriller]
Cast: Jared Harris as Edgar, Martin Donovan as Jim

ID: tt0114095
Title: Party Girl (1995)
Summary: Sassy, savvy and definitely clued-in!. Although Mary has little income, she still finds ways to spend her nights at clubs. After being arrested for throwing an illegal rave, she asks her aunt Judy for bail money. Judy then finds Mary a job at her library so that Mary can repay her. Initially, Mary finds the job as a clerk boring and stifling, and prefers to get to know a street food vendor whom she likes. However, Mary must refocus her life once she loses her job and apartment.
Genres: [Comedy]
Cast: Parker Posey as Mary, Liev Schreiber as Nigel

ID: tt0114496
Title: Something to Talk About (1995)
Summary: A story about husbands, wives, parents, children and other natural disasters.. In one of her best-ever roles, Julia Roberts is Grace, whose reaction to the infidelities of Eddie (Dennis Quaid) turns the lives and loves of the people around her into something like falling dominoes. Robert Duvall, Gena Rowlands, Kyra Sedgwick and others in "the year's best ensemble of characters" (Jack Matthews, 'Newsday') co-star in this juicy, truthful story written by Callie Khouri
Genres: [Comedy]
Cast: Julia Roberts as Grace King Bichon, Dennis Quaid as Eddie Bichon, Robert Duvall as Wyly King, Gena Rowlands as Georgia King

ID: tt0114558
Title: Strange Days (1995)
Summary: You know you want it.. Set in the year 1999 during the last days of the old millennium, the movie tells the story of Lenny Nero, an ex-cop who now deals with data-discs containing recorded memories and emotions. One day he receives a disc which contains the memories of a murderer killing a prostitute. Lenny investigates and is pulled deeper and deeper in a whirl of blackmail, murder and rape. Will he survive and solve the case?
Genres: [Crime, Drama, Science Fiction, Thriller]
Cast: Angela Bassett as Lornette 'Mace' Mason, Tom Sizemore as Max Peltier, Juliette Lewis as Faith Justin, Vincent D'Onofrio as Burton Steckler, Michael Wincott as Philo Gant, Richard Edson as Tick

ID: tt0112679
Title: Circle of Friends (1995)
Summary: . 'Circle Of Friends' is set in 1950's Ireland. The movie focuses on Benny Hogan and her best friend, Eve Malone. The story centers around Benny and Eve as they enter student life at University College, Dublin. Here Benny and Eve reunite with their childhood friend, the ice-cool Nan Mahon, the 'college belle'. They also encounter the handsome and charming Jack Foley, whom Benny quickly falls for.
Genres: [Drama, Family, Romance]
Cast: Chris O'Donnell as Jack Foley, Minnie Driver as Bernadette 'Benny' Hogan, Alan Cumming as Sean Walsh

ID: tt0109445
Title: Clerks (1994)
Summary: Just because they serve you doesn't mean they like you.. Convenience and video store clerks Dante and Randal are sharp-witted, potty-mouthed and bored out of their minds. So in between needling customers, the counter jockeys play hockey on the roof, visit a funeral home and deal with their love lives.
Genres: [Comedy]
Cast: 

ID: tt0109642
Title: Dolores Claiborne (1995)
Summary: Sometimes, an accident can be an unhappy woman's best friend.. Dolores Claiborne was accused of killing her abusive husband twenty years ago, but the court's findings were inconclusive and she was allowed to walk free. Now she has been accused of killing her employer, Vera Donovan, and this time there is a witness who can place her at the scene of the crime. Things look bad for Dolores when her daughter Selena, a successful Manhattan magazine writer, returns to cover the story.
Genres: [Crime, Drama, Mystery]
Cast: Kathy Bates as Dolores Claiborne, Jennifer Jason Leigh as Selena St. George, Christopher Plummer as John Mackey, David Strathairn as Joe St. George, John C. Reilly as Const. Frank Stamshaw, Bob Gunton as Mr. Pease

ID: tt0109707
Title: Ed Wood (1994)
Summary: When it came to making bad movies, Ed Wood was the best.. The mostly true story of the legendary "worst director of all time", who, with the help of his strange friends, filmed countless B-movies without ever becoming famous or successful.
Genres: [Comedy, Drama, History]
Cast: Johnny Depp as Ed Wood, Martin Landau as Bela Lugosi, Sarah Jessica Parker as Dolores Fuller, Patricia Arquette as Kathy O'Hara, Jeffrey Jones as Criswell, Vincent D'Onofrio as Orson Welles, Bill Murray as Bunny Breckinridge, Mike Starr as Georgie Weiss, Rance Howard as Old Man McCoy

ID: tt0113198
Title: A Goofy Movie (1995)
Summary: It's the story of a father who couldn't be closer...to driving his son crazy.. Though Goofy always means well, his amiable cluelessness and klutzy pratfalls regularly embarrass his awkward adolescent son, Max. When Max's lighthearted prank on his high-school principal finally gets his longtime crush, Roxanne, to notice him, he asks her on a date. Max's trouble at school convinces Goofy that he and the boy need to bond over a cross-country fishing trip like the one he took with his dad when he was Max's age, which throws a kink in his son's plans to impress Roxanne.
Genres: [Romance, Animation, Family, Comedy, Adventure]
Cast: Jim Cummings as Peter Pete (voice), Wallace Shawn as Principal Arthur Mazur (voice), Frank Welker as Bigfoot (voice)

ID: tt0113089
Title: Fluke (1995)
Summary: Unleash your imagination... and come along on a magical journey. Follow him home.. Workaholic Thomas Johnson dies in an auto accident and comes back to life as a dog. Remembering some of who he was, he returns to his wife and son to protect them from the man who caused his accident. But, as time goes by, he remembers more of his life, and realizes he wasn't such a good husband and father.
Genres: [Drama, Family, Fantasy]
Cast: Samuel L. Jackson as Rumbo (voice), Matthew Modine as Thomas P. Johnson / Voice of Fluke, Eric Stoltz as Jeff Newman, Ron Perlman as Sylvester, Jon Polito as Boss, Bill Cobbs as Bert

ID: tt0113234
Title: Gumby: The Movie (1995)
Summary: The original green hero!. The band is back together! Gumby reunites with The Clayboys to perform at a concert benefiting local farmers. But things take an unexpected turn when Gumby s dog, Lowbelly, reacts to the music by crying tears of real pearl! Fortune turns into disaster as Gumby s archenemies, the Blockheads, devise an elaborate scheme to dognap Lowbelly and harvest her pearls for themselves. When the Blockheads initial plan fails, they kidnap The Clayboys as well...and replace them with clones! The battle between Clayboys and clones is filled with trains and planes, knights and fights, thrills and spills. True to classic Gumby adventures, Gumby: The Movie takes viewers in and out of books, to Toyland, Camelot, outer space and beyond!
Genres: [Fantasy, Animation, Science Fiction, Family]
Cast: 

ID: tt0110116
Title: Immortal Beloved (1994)
Summary: . The life and death of the legendary Ludwig van Beethoven. Beside all the work he is known for, the composer once wrote a famous love letter to a nameless beloved and the movie tries to find out who this beloved was. Not easy as Beethoven has had many women in his life.
Genres: [Drama, Music, Romance]
Cast: Gary Oldman as Ludwig van Beethoven, Miriam Margolyes as Nanette Streicherová

ID: tt0110006
Title: Heavyweights (1995)
Summary: They don't run the fastest. They don't jump the highest. But they sure are getting the last laugh. Heavyweights. They never met a hot dog they didn't like ... until now.. Camp Hope is a summer retreat for overweight boys run by a kindly couple who make the campers feel comfortable with their extra pounds. But when tyrannical fitness guru Tony buys the camp, he puts the kids on a cruel regimen that goes too far. Sick of the endless weeks of "all work and no play," the kids stage a coup and reclaim their summer of fun.
Genres: [Comedy, Family, Drama]
Cast: Ben Stiller as Tony Perkis, Jeffrey Tambor as Maury Garner

ID: tt0103994
Title: Like Water for Chocolate (1992)
Summary: In Tita's kitchen, ordinary spices become a recipe for passion.. Tita is passionately in love with Pedro, but her controlling mother forbids her from marrying him. When Pedro marries her sister, Tita throws herself into her cooking and discovers she can transfer her emotions through the food she prepares, infecting all who eat it with her intense heartbreak.
Genres: [Drama, Romance]
Cast: 

ID: tt0110443
Title: Major Payne (1995)
Summary: He's looking for a few good men... or a few guys old enough to shave.. Major Benson Winifred Payne is being discharged from the Marines. Payne is a killin' machine, but the wars of the world are no longer fought on the battlefield. A career Marine, he has no idea what to do as a civilian, so his commander finds him a job - commanding officer of a local school's JROTC program, a bunch of ragtag losers with no hope.
Genres: [Adventure, Comedy, Family]
Cast: Michael Ironside as Lt. Col. Stone

ID: tt0113691
Title: Losing Isaiah (1995)
Summary: Who decides what makes a mother?. Khaila Richards, a crack-addicted single mother, accidentally leaves her baby in a dumpster while high and returns the next day in a panic to find he is missing. In reality, the baby has been adopted by a warm-hearted social worker, Margaret Lewin, and her husband, Charles. Years later, Khaila has gone through rehab and holds a steady job. After learning that her child is still alive, she challenges Margaret for the custody.
Genres: [Drama]
Cast: Jessica Lange as Margaret Lewin, Halle Berry as Khaila Richards, David Strathairn as Charles Lewin, Samuel L. Jackson as Kadar Lewis, Cuba Gooding Jr. as Eddie Hughes

ID: tt0113755
Title: Man of the House (1995)
Summary: Jack wants to marry Ben's mother. But there are strings attached.. Ben Archer is not happy. His mother, Sandy, has just met a man, and it looks like things are pretty serious. Driven by a fear of abandonment, Ben tries anything and everything to ruin the "love bubble" which surrounds his mom. However, after Ben and Jack's experiences in the Indian Guides, the two become much closer.
Genres: [Action, Comedy, Family]
Cast: Chevy Chase as Jack Sturgess (Squatting Dog), George Wendt as Chet Bronski (Chief Running Horse)

ID: tt0110516
Title: Milk Money (1994)
Summary: You can't get enough of a good thing... But first you have to find it.. Three young boys pool their money and pay V, a kindhearted prostitute, to strip for them. Afterward, she drives them home to the suburbs -- but then her car breaks down. It's just as well, though, because a mobster named Waltzer is after her, and V realizes the suburbs are the perfect place to hide. But things get a lot more complicated when V falls in love with Tom, a single father who is unaware of her real profession.
Genres: [Comedy, Romance]
Cast: Melanie Griffith as V, Ed Harris as Tom Wheeler, Malcolm McDowell as Waltzer, Anne Heche as Betty, Philip Bosco as Jerry the Pope

ID: tt0113808
Title: Miami Rhapsody (1995)
Summary: Love is great. Marriage is a completely different affair.. Gwyn Marcus has always wanted a marriage like her parents. She has just accepted the proposal of her boyfriend Matt, but she has misgivings about their future together. Her fear of commitment grows as she learns of the various affairs that her family is having. With her sister getting married and her brother already married, her mother is growing concerned about Gwyn's being the last single person in the family. But the more she thinks about marriage, the more she must search for the balance between career, marriage and family.
Genres: [Comedy, Romance]
Cast: Sarah Jessica Parker as Gwyn Marcus, Antonio Banderas as Antonio, Mia Farrow as Nina Marcus, Kevin Pollak as Jordan Marcus

ID: tt0110684
Title: Nobody's Fool (1994)
Summary: In a town where nothing ever happens... everything is about to happen to Sully.. Sully is a rascally ne'er-do-well approaching retirement age. While he is pressing a worker's compensation suit for a bad knee, he secretly works for his nemesis, Carl, and flirts with Carl's young wife Toby. Sully's long- forgotten son and family have moved back to town, so Sully faces unfamiliar family responsibilities. Meanwhile, Sully's landlady's banker son plots to push through a new development and evict Sully from his mother's life.
Genres: [Drama, Comedy]
Cast: Paul Newman as Sully Sullivan, Bruce Willis as Carl Roebuck, Melanie Griffith as Toby Roebuck, Philip Seymour Hoffman as Officer Raymer, Philip Bosco as Judge Flatt, Alice Drummond as Hattie, Margo Martindale as Birdy

ID: tt0114151
Title: Poison Ivy II: Lily (1996)
Summary: Passion. Seduction. Betrayal. Lily wanted to be just like Ivy.. A young and naive college art student becomes obsessed with assuming the identity and personality of a departed coed who used to live in her room, and in so doing causes complications that result in two men, a student and her art professor, lusting after her.
Genres: [Drama, Romance, Thriller]
Cast: Xander Berkeley as Donald Falk

ID: tt0114069
Title: Outbreak (1995)
Summary: This animal carries a deadly virus... and the greatest medical crisis in the world is about to happen.. A deadly airborne virus finds its way into the USA and starts killing off people at an epidemic rate. Col Sam Daniels' job is to stop the virus spreading from a small town, which must be quarantined, and to prevent an over reaction by the White House.
Genres: [Action, Drama, Science Fiction, Thriller]
Cast: Dustin Hoffman as Sam Daniels, Rene Russo as Robby Keough, Morgan Freeman as Billy Ford, Kevin Spacey as Casey Schuler, Cuba Gooding Jr. as Salt, Donald Sutherland as Donald McClintock

ID: tt0114084
Title: Panther (1995)
Summary: The People Called Them Heroes. The F.B.I. Called Them Public Enemy Number One.. Panther is a semi-historic film about the origins of The Black Panther Party for Self-Defense. The movie spans about 3 years (1966-68) of the Black Panther's history in Oakland. Panther also uses historical footage (B/W) to emphasize some points.
Genres: [Drama]
Cast: Bokeem Woodbine as Tyrone, Angela Bassett as Dr. Betty Shabazz, M. Emmet Walsh as Dorsett

ID: tt0114268
Title: Rent-a-Kid (1995)
Summary: . Harry Habert, owner of a rent office has an original idea: To rent babies from a nearby orphanage to the local families. He rents the Ward brothers to his first customers...
Genres: [Comedy, Family, TV Movie]
Cast: Leslie Nielsen as Harry Haber, Christopher Lloyd as Lawrence 'Larry' Kayvey

ID: tt0114594
Title: Swimming with Sharks (1994)
Summary: Life is not a movie. A young, naive Hollywood studio assistant finally turns the tables on his incredibly abusive producer boss.
Genres: [Crime, Comedy]
Cast: Kevin Spacey as Buddy Ackerman, Benicio del Toro as Rex

ID: tt0114788
Title: The Underneath (1995)
Summary: For passion, betrayal and murder... there's still no place like home.. Michael Chambers has come home to Austin, Texas. To the mother who's starting a new life and the brother driven by old jealousies. To the places he remembers and the memories he can't forget. And to Rachel, the woman he married and then betrayed with his passion for gambling. Now she's together with Tommy Dundee, a man no one trifles with. He takes care of her in a way Michael never could, but there's a price for his attention and Rachel knows this. And when Michael devises a plan to get Rachel out from under Tommy's control, they become entangled in a web of intrigue, danger and desire from which no one escapes unscathed.
Genres: [Thriller, Crime]
Cast: Peter Gallagher as Michael Chambers, Joe Don Baker as Clay Hinkle, Paul Dooley as Ed Dutton, Shelley Duvall as Nurse, Elisabeth Shue as Susan Crenshaw

ID: tt0112435
Title: The Baby-Sitters Club (1995)
Summary: Friends Forever. It's the story about seven very different best friends, and one summer that will bring them together like never before.
Genres: [Comedy, Drama, Family]
Cast: Bruce Davison as Watson Brewer, Ellen Burstyn as Emily Haberman, Colleen Camp as Maureen McGill

ID: tt0104779
Title: Bitter Moon (1992)
Summary: A kinky voyage with a full head of steam.. An embittered husband, paralyzed and in a wheelchair, buttonholes a complete stranger and begins to tell him the story of his marriage. The stranger would like to escape, but cannot. For one thing he grows fascinated by the story. For another he is mesmerized by the man's wife, who has perfected that trick of looking a man boldly in the eye until, by looking away, he concedes sexual supremacy.
Genres: [Thriller, Drama, Romance]
Cast: Hugh Grant as Nigel, Kristin Scott Thomas as Fiona, Peter Coyote as Oscar

ID: tt0109348
Title: Bullets Over Broadway (1994)
Summary: . Set in 1920's New York City, this movie tells the story of idealistic young playwright David Shayne. Producer Julian Marx finally finds funding for the project from gangster Nick Valenti. The catch is that Nick's girl friend Olive Neal gets the part of a psychiatrist, and Olive is a bimbo who could never pass for a psychiatrist as well as being a dreadful actress. Agreeing to this first compromise is the first step to Broadway's complete seduction of David, who neglects longtime girl friend Ellen. Meanwhile David puts up with Warner Purcell, the leading man who is a compulsive eater, Helen Sinclair, the grand dame who wants her part jazzed up, and Cheech, Olive's interfering hitman / bodyguard. Eventually, the playwright must decide whether art or life is more important.
Genres: [Action, Comedy, Thriller]
Cast: John Cusack as David Shayne, Dianne Wiest as Helen Sinclair, Jennifer Tilly as Olive Neal, Chazz Palminteri as Cheech, Mary-Louise Parker as Ellen, Jack Warden as Julian Marx, Rob Reiner as Sheldon Flender, Joe Viterelli as Nick Valenti, Jim Broadbent as Warner Purcell

ID: tt0109813
Title: The Flintstones (1994)
Summary: Yabba-Dabba-Doo!. Modern Stone Age family the Flintstones hit the big screen in this live-action version of the classic cartoon. Fred helps Barney adopt a child. Barney sees an opportunity to repay him when Slate Mining tests its employees to find a new executive. But no good deed goes unpunished.
Genres: [Fantasy, Comedy, Family]
Cast: John Goodman as Fred Flintstone, Rick Moranis as Barney Rubble, Rosie O'Donnell as Betty Rubble, Halle Berry as Rosetta Stone

ID: tt0109831
Title: Four Weddings and a Funeral (1994)
Summary: Five good reasons to stay single.. Four Weddings And A Funeral is a British comedy about a British Man named Charles and an American Woman named Carrie who go through numerous weddings before they determine if they are right for one another.
Genres: [Comedy, Drama, Romance]
Cast: Hugh Grant as Charles, Andie MacDowell as Carrie, Simon Callow as Gareth, Kristin Scott Thomas as Fiona

ID: tt0110091
Title: I Like It Like That (1994)
Summary: For Lisette, everyday has become independence day.. Lisette and husband Chino face marital difficulties. She is fed up with the kids, while he has job troubles. His mother Rosaria hates Lisette and the neighborhood tramp has designs on Chino. Things get even worse when Chino goes to jail and Lisette gets a good job uptown. Can this marriage be saved?
Genres: [Comedy, Drama, Romance]
Cast: 

ID: tt0107472
Title: The Wonderful, Horrible Life of Leni Riefenstahl (1993)
Summary: . This documentary recounts the life and work of one of most famous, and yet reviled, German film directors in history, Leni Riefenstahl. The film recounts the rise of her career from a dancer, to a movie actor to the most important film director in Nazi Germany who directed such famous propaganda films as Triumph of the Will and Olympiad. The film also explores her later activities after Nazi Germany's defeat in 1945 and her disgrace for being so associated with it which includes her amazingly active life over the age of 90.
Genres: [Documentary, Foreign]
Cast: 

ID: tt0107426
Title: Little Buddha (1993)
Summary: A magical journey to a place where the past and present meet. Little Buddha is a movie about the life of Siddhartha starring Keanu Reeves and Bridget Fonda and directed by Bernardo Bertolucci.
Genres: [Drama, History]
Cast: Keanu Reeves as Siddharta, Bridget Fonda as Lisa

ID: tt0110588
Title: Mrs. Parker and the Vicious Circle (1994)
Summary: New York in the 1920's. The only place to be was the Algonquin, and the only person to know was Dorothy Parker.. Dorothy Parker remembers the heyday of the Algonquin Round Table, a circle of friends whose barbed wit, like hers, was fueled by alcohol and flirted with despair.
Genres: [Drama]
Cast: Jennifer Jason Leigh as Dorothy Parker, Matthew Broderick as Charles MacArthur, Gwyneth Paltrow as Paula Hunt, Peter Gallagher as Alan Campbell, Andrew McCarthy as Eddie Parker, Wallace Shawn as Horatio Byrd, Martha Plimpton as Jane Grant, Lili Taylor as Edna Ferber, James Le Gros as Deems Taylor, Heather Graham as Mary Kennedy Taylor, Stephen Baldwin as Roger Spalding, Matt Malloy as Marc Connelly, Stanley Tucci as Fred Hunter

ID: tt0111054
Title: Safe Passage (1994)
Summary: Life's best journeys take you home. A mother with seven sons feels like she's losing control of her life and her family. But personal...
Genres: [Drama]
Cast: Susan Sarandon as Margaret "Mag" Singer

ID: tt0111257
Title: Speed (1994)
Summary: Get ready for rush hour. Los Angeles SWAT cop Jack Traven is up against bomb expert Howard Payne, who's after major ransom money. First it's a rigged elevator in a very tall building. Then it's a rigged bus--if it slows, it will blow, bad enough any day, but a nightmare in LA traffic. And that's still not the end.
Genres: [Action, Adventure, Crime]
Cast: Keanu Reeves as Jack Traven, Dennis Hopper as Howard Payne, Sandra Bullock as Annie Porter, Joe Morton as Capt. McMahon, Jeff Daniels as Harold 'Harry' Temple, Beth Grant as Helen, Richard Schiff as Train Driver

ID: tt0111693
Title: When a Man Loves a Woman (1994)
Summary: Through the good times. Through the bad times. When a Man Loves a Woman it's for all times.. An airline pilot and his wife are forced to face the consequences of her alcoholism when her addictions threaten her life and their daughter's safety. While the woman enters detox, her husband must face the truth of his enabling behavior.
Genres: [Drama, Romance]
Cast: Andy García as Michael Green, Meg Ryan as Alice Green, Ellen Burstyn as Emily, Philip Seymour Hoffman as Gary

ID: tt0112443
Title: Bad Company (1995)
Summary: Bribery. Blackmail. Murder. Specialities of the house.. Nelson Crowe is a CIA operative under the thumb of the Company for a disputed delivery of $50,000 in gold. They blackmail him into working for the Grimes Organization, which is set up as a private company for hire, to blackmail prominent individuals. Crowe, working with Margaret Wells (another former Covert Operations operative), blackmails and bribes a State Supreme Court judge, but the deal sours. One of Crowe's co-workers, Tod Stapp, discovers Crowe's current CIA involvement in a plot to overthrow Grimes, and blackmails him to be cut in on the deal. More blackmail occurs as Wells manipulates Crowe to kill Grimes, then the CIA uses that discovery to blackmail Wells into killing Crowe. Who can you trust???
Genres: [Action, Crime, Romance, Thriller]
Cast: Laurence Fishburne as Nelson Crowe, Ellen Barkin as Margaret Wells, David Ogden Stiers as Judge Justin Beach

ID: tt0112849
Title: Desert Winds (1997)
Summary: It began with a whisper. Jackie and Eugene are joined by a mystical wind tunnel which enables them to speak across a 500-mile desert. Believed by the Indians to be an omen of good luck, the wind inspires both characters to face their fears and follow their hearts.
Genres: []
Cast: Heather Graham as Jackie

ID: tt0113104
Title: Frank and Ollie (1995)
Summary: . Before computer graphics, special effects wizardry, and out-of-this world technology, the magic of animation flowed from the pencils of two of the greatest animators The Walt Disney Company ever produced -- Frank Thomas and Ollie Johnston. Frank and Ollie, the talent behind BAMBI, PINOCCHIO, LADY AND THE TRAMP, THE JUNGLE BOOK, and others, set the standard for such modern-day hits as THE LION KING. It was their creative genius that helped make Disney synonymous with brilliant animation, magnificent music, and emotional storytelling. Take a journey with these extraordinary artists as they share secrets, insights, and the inspiration behind some of the greatest animated movies the world has ever known!
Genres: [Documentary]
Cast: 

ID: tt0112889
Title: Dos Crímenes (1995)
Summary: . Greed and playing into the hand of providence provides the focus of this Mexican comedy adapted from a novel by Jorge Ibarguengoitia. Marcos, an architect, has just returned to the home of his wealthy uncle Ramon after squandering his money in Mexico City and subsequently finding himself falsely accused of a crime. Although he is flat-broke, he conceals this from Ramon, telling him that he has returned home to buy a local gold mine. Marcos finds the lies come easily as begins trying to induce his uncle to fund his endeavor. Irascible Ramon, who likes Marcos for his similar love of drinking and smoking is duped, but Ramon's sons are not fooled by Marcos. To them he is a threat, and they fear he will be placed in the will. Soon all of them are trying to out-manipulate each other. Even Ramon, who is not as innocent as he appears is involved in the mayhem.
Genres: []
Cast: 

ID: tt0113409
Title: In the Mouth of Madness (1994)
Summary: Lived Any Good Books Lately?. With the disappearance of hack horror writer Sutter Cane, all Hell is breaking loose...literally! Author Cane, it seems, has a knack for description that really brings his evil creepy-crawlies to life. Insurance investigator John Trent is sent to investigate Cane's mysterious vanishing act and ends up in the sleepy little East Coast town of Hobb's End.
Genres: [Horror, Mystery, Thriller]
Cast: Sam Neill as John Trent, Jürgen Prochnow as Sutter Cane, David Warner as Dr. Wrenn, Charlton Heston as Jackson Harglow, John Glover as Saperstein

ID: tt0106220
Title: Addams Family Values (1993)
Summary: The Family Just Got A Little Stranger. Siblings Wednesday and Pugsley Addams will stop at nothing to get rid of Pubert, the new baby boy adored by parents Gomez and Morticia. Things go from bad to worse when the new "black widow" nanny, Debbie Jellinsky, launches her plan to add Fester to her collection of dead husbands.
Genres: [Comedy, Family, Fantasy]
Cast: Anjelica Huston as Morticia Addams, Christopher Lloyd as Uncle Fester Addams, Christina Ricci as Wednesday Addams, Joan Cusack as Debbie Jellinsky, Carol Kane as Granny, Christine Baranski as Becky Martin-Granger, Tony Shalhoub as Jorge, Ian Abercrombie as Driver

ID: tt0109067
Title: The Air Up There (1994)
Summary: . Jimmy Dolan is a college basketball coach who wants a big promotion. To get it, he needs to make a dramatic find. He ends up deep in Africa, hoping to recruit Saleh, a huge basketball prodigy Jimmy glimpsed in a home movie. But Saleh is the chief's son and has responsibilities at home, since the tribe's land is threatened by a mining company with its own hotshot basketball team.
Genres: [Comedy, Family]
Cast: Kevin Bacon as Jimmy Dolan

ID: tt0109198
Title: Bad Girls (1994)
Summary: It was a dangerous time to be a woman. And a good time to have friends.. Four former harlots try to leave the wild west (Colorado, to be exact) and head north to make a better life for themselves. Unfortunately someone from Cody's past won't let it happen that easily.
Genres: [Action, Adventure, Comedy, Drama, Western]
Cast: Madeleine Stowe as Cody Zamora, Mary Stuart Masterson as Anita Crown, Andie MacDowell as Eileen Spenser, Drew Barrymore as Lily Laronette, James Russo as Kid Jarrett, James Le Gros as William Tucker, Robert Loggia as Frank Jarrett, Dermot Mulroney as Josh McCoy

ID: tt0109361
Title: Cabin Boy (1994)
Summary: He's Setting Sail On The High Seas... Without A Rudder, A Compass, Or A Clue!. A foul-mouthed finishing school graduate mistakenly winds up on an ill-fated fishing boat, and faces the wrath of a crew that considers him bad luck.
Genres: [Comedy]
Cast: Brian Doyle-Murray as Skunk, Brion James as Big Teddy, Ann Magnuson as Calli, Mike Starr as Mulligan, Alfred Molina as School Professor

ID: tt0106505
Title: Calendar Girl (1993)
Summary: Three boy's journey to discovery in the Hollywood of the early sixties.. Like many adolescent boys, Roy Darpinian had the hots for movie divas, and one in particular was his wet dream as half of America's in the 1950s: Marilyn Monroe. The difference is, one summer holiday he actually decided to enlist his spineless buddies, Scott Foreman and Ned Bleuer, to actually drive all the way to Hollywood and make as many desperate attempts as it takes to meet her or get arrested trying, and no setback or embarrassment (even publicly bare-ass) can stop or distract him. Against all odds, he finally even got a chance to help her...
Genres: [Drama, Comedy]
Cast: Joe Pantoliano as Harvey Darpinian, Stephen Tobolowsky as Antonio

ID: tt0106519
Title: Carlito's Way (1993)
Summary: He's got a good future if he can live past next week.. A Puerto-Rican ex-con, just released from prison, pledges to stay away from drugs and violence despite the pressure around him and lead on to a better life outside of NYC.
Genres: [Action, Crime, Drama, Thriller]
Cast: Al Pacino as Carlito Brigante, Sean Penn as David Kleinfeld, John Leguizamo as Benny Blanco, Luis Guzmán as Pachanga, Viggo Mortensen as Lalin, James Rebhorn as Norwalk

ID: tt0106660
Title: Dangerous Game (1993)
Summary: TURBULENCE REDEMPTION HOLLYWOOD. A New York film director, working on his latest movie in Los Angeles, begins to reflect the actions in his movie and real life, especially when he begins an affair with the lead actress.
Genres: [Drama]
Cast: Harvey Keitel as Eddie Israel, James Russo as Francis Burns, Victor Argo as Director of Photography

ID: tt0106673
Title: Dave (1993)
Summary: In a country where anybody can become President, anybody just did.. Bill Mitchell is the philandering and distant President of the United States. Dave Kovic is a sweet-natured and caring Temp Agency operator, who by a staggering coincidence looks exactly like the President. As such, when Mitchell wants to escape an official luncheon, the Secret Service hires Dave to stand in for him. Unfortunately, Mitchell suffers a severe stroke whilst having sex with one of his aides, and Dave finds himself stuck in the role indefinitely. The corrupt and manipulative Chief of Staff, Bob Alexander, plans to use Dave to elevate himself to the White House - but unfortunately, he doesn't count on Dave enjoying himself in office, using his luck to make the country a better place, and falling in love with the beautiful First Lady...
Genres: [Comedy]
Cast: Kevin Kline as Dave Kovic / Bill Mitchell, Sigourney Weaver as Ellen Mitchell, Kevin Dunn as Alan Reed, Ving Rhames as Duane Stevenson, Ben Kingsley as Vice-President Nance, Bonnie Hunt as White House Tour Guide, Arnold Schwarzenegger as Himself, George Martin as President's Physician

ID: tt0106677
Title: Dazed and Confused (1993)
Summary: See it with a bud.. The adventures of a group of Texas teens on their last day of school in 1976, centering on student Randall Floyd, who moves easily among stoners, jocks and geeks. Floyd is a star athlete, but he also likes smoking weed, which presents a conundrum when his football coach demands he sign a "no drugs" pledge.
Genres: [Comedy, Drama]
Cast: Matthew McConaughey as David Wooderson, Ben Affleck as Fred O'Bannion, Joey Lauren Adams as Simone Kerr, Parker Posey as Darla Marks, Nicky Katt as Clint, Renée Zellweger as Girl in blue pickup truck (uncredited)

ID: tt0106880
Title: Fear of a Black Hat (1994)
Summary: The First Drive-By Comedy. Chronicling the controversial career of bad boys N.W.H. (Niggaz With Hats), this uproarious 'mockumentary' lampoons all of hardcore rap's hot-button issues. This underground laugh riot recounts the rise, fall and resurrection of a clueless bunch of would-be rappers, Ice Cold, Tone-Def &amp; Tasty Taste performing as N.W.H.
Genres: [Comedy]
Cast: 

ID: tt0109891
Title: Getting Even with Dad (1994)
Summary: ..He's not getting mad, he's Getting Even With Dad.. Ray, an ex-con and widower, is planning a coin heist with two accomplices to help him to buy his own bakery. However, he doesn't expect his son Timmy, who was living with Ray's sister, to show up at the house right in the middle of planning. Timmy is ignored and Ray and his buddies pull off the heist. Timmy gets his father's attention by stealing the coins and hiding them. To get them back, his father must take him to a number of different places and treat him like he enjoys his presence. They grow fond of each other but Timmy won't stay with his dad unless he gives up the coins.
Genres: [Family, Comedy, Crime]
Cast: Glenne Headly as Detective Theresa Walsh

ID: tt0109913
Title: Go Fish (1994)
Summary: The girl is out there. Max is a trendy, pretty, young lesbian, who is having trouble finding love. A friend sets her up with Ely, whom Max likes, but Ely is frumpy, homely, and older. Nor do they have much in common. Can Max learn to look past the packaging?
Genres: [Drama, Romance]
Cast: 

ID: tt0109920
Title: A Good Man in Africa (1994)
Summary: . Morgan Leafy is a secretary to the British High Commissioner to an Africa nation. Leafy is a man that makes himself useful to his boss, the snobbish Arthur Fanshawe, who has no clue about what's going on around him, but who wants to use his secretary to carry on his dirty work, which involves getting one of the most powerful men in the country to do business with his country.The young secretary has an eye for beautiful women around him, especially Hazel, a native beauty, with whom he is having an affair. Things get complicated because Sam Adekunle, a man running for president of the country, wants a favor from Leafy in return after he has accepted the invitation to visit London. The proposition involves swaying a prominent doctor's opposition to a plan that will make Adenkule filthy rich.
Genres: [Comedy, Drama]
Cast: Sean Connery as Dr. Alex Murray, John Lithgow as Arthur Fanshawe, British High Commissioner, Louis Gossett, Jr. as 

ID: tt0107096
Title: Heaven & Earth (1993)
Summary: An amazing true story of survival.. Le Ly lives in a small Vietnamese village whose serenity is shattered when war breaks out. Caught between the Viet Cong and the South Vietnamese army, the village is all but destroyed. After being both brutalized and raped, Le Ly resolves to flee. She leaves for the city, surviving desperate situations, but surviving nonetheless. Eventually she meets a U.S. Marine named Steve Butler who treats her kindly and tells her he would like to be married -- maybe to her.
Genres: [History, War, Action, Drama]
Cast: Tommy Lee Jones as Steve Butler, Robert John Burke as Paul, Timothy Carhart as Big Mike

ID: tt0107286
Title: Judgment Night (1993)
Summary: Don't move. Don't whisper. Don't even breathe.. While racing to a boxing match, Frank, Mike, John and Rey get more than they bargained for. A wrong turn lands them directly in the path of Fallon, a vicious, wise-cracking drug lord. After accidentally witnessing Fallon murder a disloyal henchman, the four become his unwilling prey in a savage game of cat &amp; mouse as they are mercilessly stalked through the urban jungle in this taut suspense drama
Genres: [Action, Thriller, Crime]
Cast: Emilio Estevez as Frank Wyatt, Cuba Gooding Jr. as Mike Peterson, Denis Leary as Fallon, Jeremy Piven as Ray Cochran

ID: tt0107362
Title: Last Action Hero (1993)
Summary: This isn't the movies anymore.. Danny is obsessed with a fictional movie character action hero Jack Slater. When a magical ticket transports him into Jack's latest adventure, Danny finds himself in a world where movie magic and reality collide. Now it's up to Danny to save the life of his hero and new friend.
Genres: [Adventure, Fantasy, Action, Comedy, Family]
Cast: Arnold Schwarzenegger as Jack Slater, F. Murray Abraham as John Practice, Robert Prosky as Nick, Ian McKellen as Death, Joan Plowright as Teacher, Colleen Camp as Ratcliff, Danny DeVito as Whiskers (Voice), Jean-Claude Van Damme as Jean-Claude Van Damme, Bridgette Wilson as Whitney/Meredith, Chevy Chase as Chevy Chase, Jim Belushi as James Belushi

ID: tt0107413
Title: Life With Mikey (1993)
Summary: He's a talent agent. She's a thief. Looks like they've already got something in common.. Michael Chapman was once a child TV star. But when he grew up, he couldn't get work. So he and his brother, Ed start their own talent agency that specializes in child acts. They can't seem to find the next big thing and they have to deal with another agency who's not above bribery to get the kids to sign with them. One day Michael meets a girl named Angie and she's a real spitfire. Michael thinks she could be what they are looking for. Problem is that she has a big chip on her shoulder.
Genres: [Comedy, Family]
Cast: Michael J. Fox as Michael Chapman

ID: tt0110353
Title: Lightning Jack (1994)
Summary: . Lightning Jack Kane is an Australian outlaw in the wild west. During a bungled bank robbery he picks up mute Ben Doyle as a hostage. The two become good friends, with Jack teaching Ben how to rob banks, while they plan Jack's last heist.
Genres: [Action, Comedy, Western]
Cast: Beverly D'Angelo as Lana Castel, Cuba Gooding Jr. as Ben Doyle, Pat Hingle as U.S. Marshal Dan Kurtz, Richard Riehle as Marcus

ID: tt0107478
Title: Made in America (1993)
Summary: At the sperm-bank she asked for a tall, intelligent, black man. One out of three ain't bad.. A young black woman discovers that her father was a sperm donor, and if that weren't bad enough, he's white.
Genres: [Comedy]
Cast: Whoopi Goldberg as Sarah Mathews, Jennifer Tilly as Stacy

ID: tt0107497
Title: Malice (1993)
Summary: Her doctor wasn't playing God. He thought he was God.. A tale about a happily married couple who would like to have children. Tracy teaches infants, Andy's a college professor. Things are never the same after she is taken to hospital and operated upon by Jed, a "know all" doctor.
Genres: [Drama, Crime]
Cast: Alec Baldwin as Dr. Jed Hill, Nicole Kidman as Tracy Kennsinger, Bill Pullman as Andy Sarafian, Bebe Neuwirth as Det. Dana Harris, Anne Bancroft as Mrs. Kennsinger, Peter Gallagher as Atty. Dennis Riley, Gwyneth Paltrow as Paula Bell

ID: tt0107653
Title: Naked (1993)
Summary: . Johnny flees Manchester for London, to avoid a beating from the family of a girl he has raped. There he finds an old girlfriend, and spends some time homeless, spending much of his time ranting at strangers, and meeting characters in plights very much like his own.
Genres: [Comedy, Drama]
Cast: David Thewlis as Johnny

ID: tt0110657
Title: The Next Karate Kid (1994)
Summary: Who says the good guy has to be a guy?. During a commemoration for Japanese soldiers fighting in the US Army during World War II, Mr. Miyagi meets the widow of his commanding officer. He gets to know her granddaughter Julie, an angry teenager who is still feeling the pain of losing both her parents in an accident and is having problems with her grandmother and her fellow pupils. Mr. Miyagi decides to teach her karate to get her through her pain and issues and back on the right path.
Genres: [Adventure]
Cast: Michael Ironside as Colonel Dugan

ID: tt0107983
Title: Romeo Is Bleeding (1993)
Summary: The story of a cop who wanted it bad and got it worse.. A corrupt cop gets in over his head when he tries to assassinate a beautiful Russian hit-woman.
Genres: [Action, Crime, Drama, Thriller]
Cast: Gary Oldman as Jack Grimaldi, Annabella Sciorra as Natalie Grimaldi, Juliette Lewis as Sheri, Will Patton as Martie, James Cromwell as Cage, Michael Wincott as Sal, Dennis Farina as Nick Gazzara, Ron Perlman as Jack's Lawyer

ID: tt0108065
Title: Searching for Bobby Fischer (1993)
Summary: Every journey begins with a single move.. A prepubescent chess prodigy refuses to harden himself in order to become a champion like the famous but unlikable Bobby Fischer.
Genres: [Drama]
Cast: Joe Mantegna as Fred Waitzkin, Ben Kingsley as Bruce Pandolfini, Laurence Fishburne as Vinnie, David Paymer as Kalev, William H. Macy as Tunafish Father, Dan Hedaya as Tournament Director

ID: tt0108162
Title: Sliver (1993)
Summary: You like to watch... don't you?. A women moves into an apartment in Manhattan and learns that the previous tenant's life ended mysteriously after the tenant fell from the balcony.
Genres: [Drama, Thriller]
Cast: Sharon Stone as Carly Norris, Colleen Camp as Judy Marks, Martin Landau as Alex Parsons

ID: tt0108174
Title: So I Married an Axe Murderer (1993)
Summary: The honeymoon was killer.. Just after a bad breakup, Charlie MacKenzie falls for lovely butcher Harriet Michaels and introduces her to his parents. But, as voracious consumers of sensational tabloids, his parents soon come to suspect that Harriet is actually a notorious serial killer -- "Mrs. X" -- wanted in connection with a string of bizarre honeymoon killings. Thinking his parents foolish, Charlie proposes to Harriet. But while on his honeymoon with her, he begins to fear they were right.
Genres: [Comedy, Crime, Romance]
Cast: Amanda Plummer as Rose Michaels, Phil Hartman as Ranger John 'Vicky' Johnson - Alcatraz Guide, Debi Mazar as Susan, Tony's Girlfriend, Alan Arkin as Tony's Boss / Precinct Capt. (uncredited)

ID: tt0110763
Title: The Pagemaster (1994)
Summary: All The Adventure Your Imagination Can Hold.. Tyler knows a lot about accidents. So much so, he is scared to do anything that might endanger him, like riding his bike, or climbing into his treehouse. While in an old library, he is mystically transported into the unknown world of books, and he has to try and get home again. Along the way he meets some interesting characters, like Fantasy, Adventure, and Horror... Written by Colin Tinto
Genres: [Fantasy, Science Fiction, Family, Animation]
Cast: Christopher Lloyd as Mr. Dewey, Patrick Stewart as Adventure (voice), Whoopi Goldberg as Fantasy (voice), Ed Begley Jr. as Alan Tyler, Frank Welker as Horror (voice), Phil Hartman as Tom Morgan (voice), Jim Cummings as Long John Silver (voice)

ID: tt0111709
Title: Wide Eyed and Legless (1993)
Summary: . Diana and Deric have an ideal marriage: they thrive in each other's company, they're funny, and they enjoy their two grown children and Deric's dotty mother; the trouble is, Diana can no longer walk and her malady defies medical diagnosis. To care for Diana, Deric is letting his business slide, but at a civic luncheon, he is seated next to Aileen Armitage, a novelist who is blind. They have a nice time, and on the sly, Diana contacts Aileen to made an odd request. Diana's declining health and her resolve bring this triangle of unlikely friends to a surprising place.
Genres: [TV Movie, Drama, Romance, Comedy]
Cast: Jim Broadbent as Deric Longden

ID: tt0112572
Title: The Brady Bunch Movie (1995)
Summary: . The original 70's TV family is now placed in the 1990's, where they're even more square and out of place than ever.
Genres: [Comedy]
Cast: David Graf as Sam Franklin, Michael McKean as Mr. Larry Dittmeyer

ID: tt0029583
Title: Snow White and the Seven Dwarfs (1937)
Summary: The Happiest, Dopiest, Grumpiest, Sneeziest movie of the year.. A beautiful girl, Snow White, takes refuge in the forest in the house of seven dwarfs to hide from her stepmother, the wicked Queen. The Queen is jealous because she wants to be known as "the fairest in the land," and Snow White's beauty surpasses her own.
Genres: [Fantasy, Animation, Family]
Cast: 

ID: tt0065214
Title: The Wild Bunch (1969)
Summary: If you only want to spend two hours in a movie theatre and go home and forget it, stay away from THE WILD BUNCH.. Aging outlaw Pike Bishop (William Holden) prepares to retire after one final robbery. Joined by his gang, which includes Dutch Engstrom (Ernest Borgnine) and brothers Lyle (Warren Oates) and Tector Gorch (Ben Johnson), Bishop discovers the heist is a setup orchestrated in part by his old partner, Deke Thornton (Robert Ryan). As the remaining gang takes refuge in Mexican territory, Thornton trails them, resulting in fierce gunfights with plenty of casualties
Genres: [Adventure, Western]
Cast: Ernest Borgnine as Dutch Engstrom, Dub Taylor as Reverend Wainscoat

ID: tt0117247
Title: One Fine Day (1996)
Summary: She was having a perfectly bad day... Then he came along and spoiled it.. Melanie Parker, an architect and mother of Sammy, and Jack Taylor, a newspaper columnist and father of Maggie, are both divorced. They meet one morning when overwhelmed Jack is left unexpectedly with Maggie and forgets that Melanie was to take her to school. As a result, both children miss their school field trip and are stuck with the parents. The two adults project their negative stereotypes of ex-spouses on each other, but end up needing to rely on each other to watch the children as each must save his job. Humor is added by Sammy's propensity for lodging objects in his nose and Maggie's tendency to wander.
Genres: [Comedy, Romance]
Cast: Michelle Pfeiffer as Melanie Parker, George Clooney as Jack Taylor, Charles Durning as Lew, Joe Grifasi as Manny Feldstein, George Martin as Mr. Smith Leland, Holland Taylor as Rita, Michael Badalucco as Lt. Bonomo

ID: tt0110374
Title: Loaded (1996)
Summary: . A group of young friends convene in the countryside to shoot a horror movie. But an experiment with LSD sees normal boundaries between them collapsing, and tragedy subsequently striking.
Genres: [Drama, Mystery, Thriller]
Cast: 

ID: tt0114671
Title: Land and Freedom (1995)
Summary: The fight against fascism begins with the fight against Stalinism.. The movie narrates the story of David Carr, an unemployed worker and member of the Communist Party of Great Britain. In 1936 he decides to fight for the Republican side in the Spanish Civil War, an anti-fascist coalition of liberals, communists and anarchists. Similar to George Orwell's experiences documented in Homage to Catalonia, he joins the POUM worker militia and witnesses first hand the betrayal of the Spanish revolution by the Stalinists, loyal only to the dictat of Moscow.
Genres: [Drama, History, War, Romance]
Cast: Ian Hart as David Carr

ID: tt0112509
Title: Billy's Holiday (1995)
Summary: As of today, he's not the man he used to be!. This movie is all about a normal guy called Billy Apples. He is a social drinker and sometimes lead singer of a band. While he is doing all of this, he still manages to bring up his teenage daughter. One night while sitting at home Billy finds that he has a talent! He finds that he is able to sing like his idol, 50's singer Billie Holiday. From there he is going places, from huge live performances to even cutting a hit record, but in the end he must make a choice between the life he has, and the life he once knew. Written by Graham Wilson Jr
Genres: []
Cast: 

ID: tt0115591
Title: August (1996)
Summary: It's really how life can appear with desillusions, good and funny moments, peace, love and hate.. Set in 1890s North Wales over a long, hot August weekend, the Victorian calm of a household is suddenly upset with the arrival of a London couple who impose their city ways and thoughts on the more rurally based family. An adaptation of Anton Chekhov's play, "Uncle Vanya."
Genres: [Romance, Comedy, Drama]
Cast: Anthony Hopkins as Ieuan Davies

ID: tt0114787
Title: Underground (1995)
Summary: . Black marketeers Marko (Miki Manojlovic) and Blacky (Lazar Ristovski) manufacture and sell weapons to the Communist resistance in WWII Belgrade, living the good life along the way. Marko's surreal duplicity propels him up the ranks of the Communist Party, and he eventually abandons Blacky and steals his girlfriend. After a lengthy stay in a below-ground shelter, the couple reemerges during the Yugoslavian Civil War of the 1990s as Marko realizes that the situation is ripe for exploitation.
Genres: [War, Drama, Comedy]
Cast: 

ID: tt0113720
Title: All Things Fair (1995)
Summary: He was a student. She was his teacher. Their love was forbidden.. This film is set in 1943 when the whole of Europe was embroiled in WWII. It deals with attraction of a 15 year old boy Stig to his teacher Viola. The whole movie revolves around the sexual encounters between Stig and Viola and how he eventually grows out of it.
Genres: [War, Drama, Romance]
Cast: 

ID: tt0048473
Title: Pather Panchali (1955)
Summary: . The film is a coming-of-age story of a young boy named Apu, and life in his small Indian village in the early years of the 20th century. Apu is born to a poor Brahmin family in Bengal. His father Harihar is a priest who dreams of becoming a successful poet and a playwright; he does not earn enough, but the mother Sarbajaya keeps the family going. Because of their limited resources, Sarbajaya resents having to share her home with Harihar's elderly cousin: the old and helpless cripple Indir. Apu's sister Durga is always getting into trouble for stealing guavas from the neighbour's orchards for Indir. She cares for Apu like a good older sister but loves to affectionately tease him. Together, they make do with what they have and enjoy the simple joys of life.
Genres: [Drama]
Cast: 

ID: tt0117128
Title: Mystery Science Theater 3000: The Movie (1996)
Summary: Every year Hollywood makes hundreds of movies. This is one of them.. The mad and evil scientist, Dr. Clayton Forrester, has created an evil little scheme that is bound to give him world global domination but first things first. He plans to torment Mike Nelson and the robots by sending them a real stinker of a film to watch called, "This Island Earth." He is convinced that this movie will drive them insane. Will this be the ultimate cheese that breaks the boys' spirits?
Genres: [Comedy, Science Fiction]
Cast: 

ID: tt0082206
Title: Coup de Torchon (1981)
Summary: . In 1938 in the french West Africa, Lucien Cordier is a weak and corrupted policeman despised by the local bad boys. His wife is openly cheating on him and he hasn't got any self-respect anymore. But when comes the occasion, his revenge will be terrible.
Genres: [Drama]
Cast: 

ID: tt0111430
Title: Tigrero: A Film That Was Never Made (1994)
Summary: . In 1993, Sam Fuller takes Jim Jarmusch on a trip into Brazil's Mato Grosso, up the River Araguaia to the village of Santa Isabel Do Morro, where 40 years before, Zanuck had sent Fuller to scout a location and write a script for a movie based on a tigrero, a jaguar hunter. Sam hopes to find people who remember him, and he takes film he shot in 1954. He's Rip Van Winkle, and, indeed, a great deal changed in the village. There are televisions, watches, and brick houses. But, the same Karajá culture awaits as well. He gathers the villagers to show his old film footage, and people recognize friends and relatives, thanking Fuller for momentarily bringing them back to life.
Genres: [Documentary, Foreign]
Cast: 

ID: tt0107727
Title: The Eye of Vichy (1993)
Summary: . Directed by French filmmaker Claude Chabrol, this documentary examines Nazi and Vichy newsreels and propaganda films from World War II meant to turn the French against the Jews and the Allied Forces and into Nazi sympathizers.  This movie is only made of archive pictures: the official newsreels that were broadcasted on French movie screens during 1940 and 1944 (the Occupation).
Genres: []
Cast: 

ID: tt0114736
Title: True Crime (1996)
Summary: Trust no one.. Mary Giordano is a bright, intelligent student who goes to a catholic school. She also has a addiction to mystery novels and detective magazines (hence the title of the movie), which inspire her to do her own detective work. When she starts snooping around on the case of a murderer of teenage girls, it gets her in hot water with her mentor Detective Jerry Gunn. But it also starts a team up with police cadet Tony Campbell. The two work together to find the murderer. But the closer Mary gets to solving the murder, the more danger she puts herself in of being the next victim.
Genres: [Crime, Mystery, Thriller]
Cast: Bill Nunn as Detective Jerry Guinn

ID: tt0112604
Title: Butterfly Kiss (1995)
Summary: . Eunice is walking along the highways of northern England from one filling station to another. She is searching for Judith, the woman, she says to be in love with. It's bad luck for the women at the cash desk not to be Judith, because Eunice is eccentric, angry and extreme dangerous. One day she meets Miriam, hard of hearing and a little ingenuous, who feels sympathy for Eunice and takes her home. Miriam is very impressed by Eunice's fierceness and willfulness and follows her on the search for Judith. Shocked by Eunice's cruelty she tries to make her a better person, but she looses ground herself.
Genres: [Crime, Drama, Romance, Thriller]
Cast: Amanda Plummer as Eunice

ID: tt0095776
Title: Oliver & Company (1988)
Summary: The first Disney movie with attitude.. This animated take on Oliver Twist re-imagines Oliver as an adorable orphaned kitten who struggles to survive in New York City and falls in with a band of canine criminals led by an evil human. First, Oliver meets Dodger, a carefree mutt with street savoir faire. But when Oliver meets wealthy Jenny on one of the gang's thieving missions, his life changes forever.
Genres: [Animation, Comedy, Family]
Cast: Cheech Marin as Tito (voice), Roscoe Lee Browne as Francis (voice), Dom DeLuise as Fagin (voice), Robert Loggia as Sykes (voice)

ID: tt0073778
Title: Switchblade Sisters (1975)
Summary: So Easy to Kill, So Hard to Love. A tough gang of teenage girls are looking for love and fighting for turf on the mean streets of the city! Bad girls to the core, these impossibly outrageous high school hoodlums go where they want ... and create mayhem wherever they go!
Genres: [Action, Crime, Drama]
Cast: 

ID: tt0108500
Title: The Visitors (1993)
Summary: They Weren't Born Yesterday!. This outrageous time-travel comedy follows the misadventures of a wacky medieval knight (Jean Reno) and his faithful servant when they are accidentally transported to contemporary times by a senile sorcererMayhem rules as these 12th-century visitors try adapting to the wildly confusing modern world. To avoid being stuck here for good, however, they soon begin an all-out cosmic assault on their former castle -- now a luxury hotel -- in their quest to return to the past
Genres: [Fantasy, Comedy, Science Fiction]
Cast: Jean Reno as Godefroy de Papincourt

ID: tt0113270
Title: The Haunted World of Edward D. Wood, Jr. (1996)
Summary: Can your MIND stand the SHOCKING TRUTH?. The strange life and the wonderfully awful films of 1950's Hollywood Z movie director Ed Wood are profiled in this documentary that was conceived of and researched several years before commercial-filmmaker Tim Burton made his feature film tribute. Actually, Wood does not appear much in this film. Rather, it centers on the lives and thoughts of his entourage and those who knew him. Among those interviewed are Wood's former lover and star of his earliest films, Dolores Fuller, whom he abruptly replaced in the middle of Bride of the Monster with actress Loretta King who is also interviewed.
Genres: [Documentary]
Cast: Bela Lugosi as Himself

ID: tt0333373
Title: Two Friends (2002)
Summary: . Two Sicilian friends, Nunzio and Pino, share the same apartment in Turin. Nunzio works in a factory but is laid off because of his illness. Pino, on the other hand, is a mysterious man and he is always traveling because of his work. Nunzio would very much like to know what his friend is doing for a living but Pino will not tell him. Nunzio spends his free time the best he can in his friend's absences. He ends up falling in love with Maria, a commercial employee, whereas his health condition deteriorates...
Genres: [Drama, Foreign]
Cast: 

ID: tt0117260
Title: Original Gangstas (1996)
Summary: The original bad boys are stepping up in '96!. A violent street gang, the Rebels, rule the streets of Gary, Indiana. The Rebels shoot Marvin Bookman, a store-keeper, for giving the police information about a drive-by shooting they committed. Marvin's son, former NFL star John who created the Rebels, returns to Gary to be with his father and, with a little help from his friends, to destroy the Rebels his way.
Genres: [Action, Drama, Crime, Thriller]
Cast: Charles Napier as Mayor

ID: tt0116405
Title: Getting Away with Murder (1996)
Summary: One bad apple can spoil the whole line-up.. When the very moralistic college ethics instructor Jack Lambert finds himself living next door to an accused German death camp commander, he takes it upon himself to rid the world of this man.
Genres: [Comedy]
Cast: Jack Lemmon as , Dan Aykroyd as , Bonnie Hunt as Gail Holland, Lily Tomlin as Inga Mueller

ID: tt0112586
Title: Brothers in Trouble (1995)
Summary: . Amir is an illegal Pakistani immigrant smuggled into England in the 1960's to work, to send money to his family and perhaps even bring them over with him. A skilled laborer, he is forced to do unskilled work like shoveling sheep dung and processing wool. He lives in a boarding house with nearly a dozen other men, under the supervision of Hussein Shah. He befriends a young student, Sakib, who dreams of being a writer. Their existence is punctuated by secret movies, a visiting prostitute, fear of detection and deportation, and the gangster-like smuggler who comes by for his take every week. The household is shaken up by the arrival of a white girl, Shah's girlfriend, and the sense of femininity and family she brings.
Genres: [Comedy, Drama]
Cast: 

ID: tt0057012
Title: Dr. Strangelove or: How I Learned to Stop Worrying and Love the Bomb (1964)
Summary: The hot-line suspense comedy. Insane General Jack D. Ripper initiates a nuclear strike on the Soviet Union. As soon as the actions of General "Buck" Turgidson are discovered, a war room full of politicians, generals and a Russian diplomat all frantically try to stop the nuclear strike. Near the end is a scene that is probably the most uniquely unforgettable performance of Slim Pickens in his movie career. Peter Sellers plays multiple roles in this film.
Genres: [Drama, Comedy, War]
Cast: Keenan Wynn as Colonel Bat Guano, James Earl Jones as Lt. Lothar Zogg

ID: tt0116669
Title: Jack (1996)
Summary: Jack Powell is about to tackle his biggest adventure ever... the fifth grade!. Jack Powell suffers from an affliction that makes him grow four times faster than normal, so the 10-year-old looks like a 40-year-old man. After years of being tutored at home, Jack convinces his overprotective parents to send him to public school. The children don't know what to make of Jack, but with the help of his fifth-grade teacher, he makes an effort to win them over.
Genres: [Comedy, Drama, Science Fiction]
Cast: Robin Williams as Jack Charles Powell, Jennifer Lopez as Miss Marquez

ID: tt0063715
Title: Spirits of the Dead (1968)
Summary: The ultimate orgy of evil. Three directors each adapt a Poe short story to the screen: "Toby Dammit" features a disheveled drugged and drunk English movie star who nods acceptance in the Italian press and his producers fawn over him. "Metzengerstein" features a Mediveal countess who has a love-hate relationship with a black stallion - who it turns out is really her dead lover. "William Wilson" tells the story of a sadistic Austrian student with an exact double whom he later kills.
Genres: [Drama, Mystery, Horror]
Cast: Terence Stamp as Toby Dammit (segment "Toby Dammit"), Vincent Price as Narrator (English version) (uncredited)

ID: tt0116629
Title: Independence Day (1996)
Summary: Earth. Take a good look. It might be your last.. On July 2, a giant alien mothership enters orbit around Earth and deploys several dozen saucer-shaped 'destroyer' spacecraft that quickly lay waste to major cities around the planet. On July 3, the United States conducts a coordinated counterattack that fails. On July 4, a plan is devised to gain access to the interior of the alien mothership in space, in order to plant a nuclear missile.
Genres: [Action, Adventure, Science Fiction]
Cast: Bill Pullman as President Thomas J. Whitmore, Jeff Goldblum as David Levinson, Mary McDonnell as First Lady Marilyn Whitmore, Robert Loggia as General William Grey, Randy Quaid as Russell Casse, Vivica A. Fox as Jasmine Dubrow, James Rebhorn as Albert Nimzicki, Brent Spiner as Dr. Brackish Okun

ID: tt0118113
Title: Walking and Talking (1996)
Summary: A movie for everyone who wants to get married and stay single at the same time.. Things have been tough lately for Amelia. Her best friend moved out of the apartment, her cat got cancer, and now her best friend, Laura, is getting married. She copes with things, from the help of Andrew, Frank, Laura, and a brief romance with Bill "The Ugly Guy".
Genres: [Comedy, Drama, Romance]
Cast: Anne Heche as Laura, Liev Schreiber as Andrew, Alice Drummond as Betsy, Kevin Corrigan as Bill

ID: tt0116531
Title: High School High (1996)
Summary: A Dangerous Mind is a terrible thing to waste. Richard Clark has just left the well-known Wellington Academy to teach at Marion Barry High School. Now, he will try to inspire the D-average students into making good grades and try to woo a fellow teacher.
Genres: [Comedy]
Cast: Jon Lovitz as Richard Clark, Louise Fletcher as Schuldirektorin Evelyn Doyle

ID: tt0117008
Title: Matilda (1996)
Summary: A little magic goes a long way.. A grouchy couple are parents to a very sweet girl, Matilda. Unlike her bratty brother and mean parents, Matilda becomes a very sweet and extremely intelligent 6 year old girl, who is very keen to go to school and read books. After a while, her parents send her to school with the worst principal in the world, a very sweet teacher, and good friends. While trying to put up with her parents' and principal's cruelty, she starts to unwittingly unleash telekinetic powers, destroying a television and making a newt fly onto the principal. With enough practice, Matilda starts to learn to control her telekinetic powers and soon using them on her principal so she can drive her away from the school.
Genres: [Comedy, Family, Fantasy]
Cast: Danny DeVito as Harry Wormwood, Paul Reubens as FBI Agent Bob, Tracey Walter as FBI Agent Bill

ID: tt0115836
Title: Carpool (1996)
Summary: A dad in a hurry. A man on the run. And five kids with an attitude.. A man with an important business meeting finds himself having to take care of the carpool for the neighborhood school children when his wife gets sick. Stopping to get donuts for the kids, things go even more awry when he finds himself a victim of a robbery. However, the situation only gets worse as a desperate man who had been contemplating a bank robbery robs the robbers and takes the man and the kids hostage in their van as his truck is blocked by an armored car. The thing then proceeds into a comedic chase movie. The father finds his kids don't really respect him and they react better to the robber. The end result is everyone gets a lifestyle change, including the original store owner.
Genres: [Comedy, Family]
Cast: Tom Arnold as Franklin Laszlo, David Paymer as Daniel Miller, Rod Steiger as Mr. Hammerman

ID: tt0075169
Title: Shadow of Angels (1976)
Summary: . Beautiful, detached, laconic, consumptive Lily Brest is a streetwalker with few clients. She loves her idle boyfriend Raoul who gambles away what little she earns. The town's power broker, called the rich Jew, discovers she is a good listener, so she's soon busy. Raoul imagines grotesque sex scenes between Lily and the Jew; he leaves her for a man. Her parents, a bitter Fascist who is a cabaret singer in drag and her wheelchair-bound mother, offer no refuge. Even though all have a philosophical bent, the other whores reject Lily because she tolerates everyone, including men. She tires of her lonely life and looks for a way out. Even that act serves the local corrupt powers.
Genres: [Drama]
Cast: 

ID: tt0112607
Title: Bye-Bye (1995)
Summary: . In Paris, Ismaél, a young Tunisian, cares for two brothers, Nouredine, a cripple, and streetwise Mouloud, 14. In haste, Ismaél and Mouloud go to Marseilles where an uncle lives. Nouredine has died in a fire, and Ismaél feels guilt on top of grief. Ismaél becomes friends with Jacky, a white man whose father and brother hate immigrants. Mouloud hangs out with cousin Rhida who breaks Islamic rules and deals hash. Ismaél decides Mouloud must return to Tunisia, but the boy runs off, becoming an acolyte to Rhida's supplier. Ismaél and Jacky's Arab girlfriend start an affair, friends betray friends, and the racism gets ugly. Can Ismaél rescue himself and Mouloud or will life in France crush them?
Genres: [Drama]
Cast: 

ID: tt0109001
Title: 1-900 (1994)
Summary: . Sarah, 30, single, well educated, likes art, places a voice ad for phone sex, inviting replies from men under 35, sturdy and sensual. Wilbert, a chubby middle aged architect, leaves a message that he is Thomas, 32 and well formed. A week later, she phones. She likes his voice, he likes her laugh, so once a week she phones him (he's not to know her last name, where she lives, or her number). Portraits emerge of humor and sadness. He presses for details of her life and tells her about his work; she wants these Thursday calls to be fantasy and release, separate from the rest of who she is. The film dramatizes these tensions of lies and truth, connection and distance. Written by
Genres: []
Cast: 

ID: tt0035896
Title: For Whom the Bell Tolls (1943)
Summary: Thunderous! Tender! Touching!. Spain in the 1930s is the place to be for a man of action like Robert Jordan. There is a civil war going on and Jordan who has joined up on the side that appeals most to idealists of that era -- like Ernest Hemingway and his friends -- has been given a high-risk assignment up in the mountains. He awaits the right time to blow up a bridge in a cave.
Genres: [Drama, History, Romance, Thriller]
Cast: 

ID: tt0045152
Title: Singin' in the Rain (1952)
Summary: What a Glorious Feeling!. In 1927 Hollywood, Don Lockwood and Lina Lamont are a famous on-screen romantic pair in silent movies, but Lina mistakes the on-screen romance for real love. When their latest film is transformed into a musical, Don has the perfect voice for the songs, but strident voice faces the studio to dub her voice. Aspiring actress, Kathy Selden is brought in and, while she is working on the movie, Don falls in love with her.
Genres: [Comedy, Music, Romance]
Cast: Bess Flowers as Audience Member (uncredited), Kathleen Freeman as Phoebe Dinsmore (uncredited)

ID: tt0053604
Title: The Apartment (1960)
Summary: Movie-wise, there has never been anything like it - laugh-wise, love-wise, or otherwise-wise!. Bud Baxter is a minor clerk in a huge New York insurance company, until he discovers a quick way to climb the corporate ladder. He lends out his apartment to the executives as a place to take their mistresses. Although he often has to deal with the aftermath of their visits, one night he's left with a major problem to solve.
Genres: [Comedy, Drama, Romance]
Cast: Jack Lemmon as C.C. Baxter, Shirley MacLaine as Fran Kubelik

ID: tt0053291
Title: Some Like It Hot (1959)
Summary: The movie too HOT for words!. Two musicians witness a mob hit and struggle to find a way out of the city before they are found by the gangsters. Their only opportunity is to join an all-girl band as they leave on a tour. To make their getaway they must first disguise themselves as women, then keep their identities secret and deal with the problems this brings - such as an attractive bandmate and a very determined suitor.
Genres: [Comedy, Romance]
Cast: Jack Lemmon as Jerry (Daphne), Arthur Tovey as Speakeasy Patron (uncredited)

ID: tt0032484
Title: Foreign Correspondent (1940)
Summary: The thrill spectacle of the year!. The European war was only beginning to erupt across national borders. Its titular hero, Johnny Jones, is an American crime reporter dispatched by his New York publisher to put a fresh spin on the drowsy dispatches emanating from overseas, his nose for a good story promptly leading him to the crime of fascism and Nazi Germany's designs on European conquest In attempting to learn more about a seemingly noble peace effort, Jones who walks into the middle of an assassination, uncovers a spy ring, and, not entirely coincidentally, falls in love.
Genres: [Mystery, Thriller, War]
Cast: Gino Corrado as Luncheon Waiter, Sam Harris as Luncheon Guest, Alfred Hitchcock as Man with Newspaper on Street

ID: tt0048960
Title: Around the World in Eighty Days (1956)
Summary: It's a wonderful world, if you'll only take the time to go around it!. Based on the famous book by Jules Verne the movie follows Phileas Fogg on his journey around the world. Which has to be completed within 80 days, a very short period for those days.
Genres: [Adventure, Comedy, Family, Fantasy, Romance]
Cast: Peter Lorre as Steward, Shirley MacLaine as Princess Aouda, John Gielgud as Foster, Bess Flowers as Extra (uncredited), Sam Harris as Extra (uncredited), Arthur Tovey as Extra (uncredited)

ID: tt0038300
Title: Angel on My Shoulder (1946)
Summary: With an ANGEL on his shoulder... And the DEVIL in his heart!. Gangster Eddie Kagel is killed by a trusted lieutenant and finds himself in Harry Redmond Jr's special effects Hell, where Nick/The Devil sees that he is an-exact double for a judge of whom Nick doesn't approve. Eddie is agreeable to having his soul transferred to the judge's body, as it will give him a chance to avenge himself on his killer. But every action taken by Eddie (as the judge) results in good rather than evil and, to Nick's dismay, the reputation and influence of the judge is enhanced, rather than impaired by Eddie. And Eddie also falls in love with the judge's fiancée, Barbara. Even Eddie's planned revenge fails and Nick is forced to concede defeat. He returns to Hell, taking Eddie with him, after Eddie has extracted his promise that Nick will not molest the judge or Barbara in the future.
Genres: [Comedy, Fantasy, Romance]
Cast: 

ID: tt0116442
Title: Grace of My Heart (1996)
Summary: For years her songs brought fame to other people. Then she found her own voice.. An aspiring singer, Denise Waverly/Edna Buxton, sacrifices her own singing career to write hit songs that launch the careers of other singers. The film follows her life from her first break, through the pain of rejection from the recording industry and a bad marriage, to her final triumph in realizing her dream to record her own hit album.
Genres: [Drama, Comedy, Music]
Cast: Illeana Douglas as Denise Waverly / Edna Buxton, John Turturro as Joel Millner, Eric Stoltz as Howard Cazsatt, Matt Dillon as Jay Phillips, Bridget Fonda as Kelly Porter, Richard Schiff as Audition Record Producer, Bruce Davison as John Murray, Lucinda Jenney as Marion

ID: tt0117473
Title: The Rich Man's Wife (1996)
Summary: Someone Is Playing A Very Deadly Game.... A rich man's wife finds she has a bad prenuptial agreement with an even worse husband. Over drinks with a stranger, she fantasizes about doing her husband in to void the prenupt. The stranger decides to turn her imagination into reality much to the wife's surprise.
Genres: [Drama, Thriller]
Cast: Halle Berry as Josie Potenza, Frankie Faison as Detective Ron Lewis, Christopher McDonald as Tony Potenza

ID: tt0116167
Title: Ed's Next Move (1996)
Summary: It's the hardest move a guy has to make.. Ed lives in Wisconsin, but he's just been dumped by his girlfriend. So he moves to New York City for a big change...but will he ever find love again? He loves his new job studying the genetics of different strains of rice. He has a good partnership with his promiscuous roommate Ray. And he's made some great friends, characters at the local diner. But then there's Natalie (Lee for short). She's just what Ed thinks he's looking for, and she's available...or is she?
Genres: []
Cast: 

ID: tt0116421
Title: The Glimmer Man (1996)
Summary: Two good cops. One bad situation.. A former government operative renowned for his stealth, Jack Cole is now a Los Angeles police detective. When a series of horrible murders occurs in the metro area, Cole is assigned to the case, along with tough-talking fellow cop Jim Campbell. Although the two men clash, they gradually become effective partners as they uncover a conspiracy linked to the killings, which also involves terrorism and organized crime.
Genres: [Action, Adventure, Crime, Drama, Thriller]
Cast: Bob Gunton as Frank Deverell, Brian Cox as Mr. Smith, Stephen Tobolowsky as Christopher Maynard

ID: tt0064603
Title: The Love Bug (1968)
Summary: Herbie will honk his way into your heart.. Herbie is a car - but no ordinary car. The story follows the Volkswagen Beetle with a mind of its own from the showroom to the race track, with various close escapes in between. Three further Herbie movies were to follow.
Genres: [Comedy, Family, Fantasy]
Cast: 

ID: tt0059793
Title: That Darn Cat! (1965)
Summary: It takes a Siamese secret agent to unravel the PURR-fect crime!. A young woman suspects foul play when her cat comes home wearing a wrist watch. Convincing the FBI, though, and catching the bad guys is tougher than she imagined.
Genres: [Drama, Family, Comedy]
Cast: Roddy McDowall as Gregory Benson

ID: tt0038166
Title: The Three Caballeros (1944)
Summary: . For Donald's birthday he receives a box with three gifts inside. The gifts, a movie projector, a pop-up book, and a pinata, each take Donald on wild adventures through Mexico and South America.
Genres: [Animation, Family, Music]
Cast: 

ID: tt0102798
Title: Robin Hood: Prince of Thieves (1991)
Summary: For the good of all men, and the love of one woman, he fought to uphold justice by breaking the law.. When the dastardly Sheriff of Nottingham murders Robin's father, the legendary archer vows vengeance. To accomplish his mission, Robin joins forces with a band of exiled villagers (and comely Maid Marian), and together they battle to end the evil sheriff's reign of terror.
Genres: [Adventure]
Cast: Kevin Costner as Robin Hood, Morgan Freeman as Azeem, Christian Slater as Will Scarlett, Michael Wincott as Guy of Gisborne, Sean Connery as King Richard (uncredited)

ID: tt0058331
Title: Mary Poppins (1964)
Summary: It's supercalifragilisticexpialidocious!. The movie combines a diverting story, songs, color and sequences of live action blended with the movements of animated figures. Mary Poppins is a kind of Super-nanny who flies in with her umbrella in response to the request of the Banks children and proceeds to put things right with the aid of her rather extraordinary magical powers before flying off again.
Genres: [Comedy, Family, Fantasy]
Cast: 

ID: tt0095016
Title: Die Hard (1988)
Summary: 40 Stories. Twelve Terrorists. One Cop.. NYPD cop, John McClane's plan to reconcile with his estranged wife is thrown for a serious loop when minutes after he arrives at her office, the entire building is overtaken by a group of terrorists. With little help from the LAPD, wisecracking McClane sets out to single-handedly rescue the hostages and bring the bad guys down.
Genres: [Action, Thriller]
Cast: Bruce Willis as John McClane, Bonnie Bedelia as Holly Gennero McClane, William Atherton as Richard Thornburg, Grand L. Bush as FBI Agent Little Johnson, Mary Ellen Trainor as Gail Wallens

ID: tt0114597
Title: Synthetic Pleasures (1995)
Summary: technology control passion. Conceived as an electronic road movie, this documentary investigates cutting edge technologies and their influence on our culture as we approach the 21st century. It takes off from the idea that mankind's effort to tap the power of Nature has been so successful that a new world is suddenly emerging,an artificial reality. Virtual Reality, digital and biotechnology, plastic surgery and mood-altering drugs promise seemingly unlimited powers to our bodies, and our selves. This film presents the implications of having access to such power as we all scramble to inhabit our latest science fictions.
Genres: [Documentary]
Cast: 

ID: tt0120107
Title: Shadow Conspiracy (1997)
Summary: . Bobby Bishop (Sheen) is a special assistant to the President of the United States. Accidentally, he meets his friend professor Pochenko on the street. Pochenko has time to tell Bishop about some conspiracy in the White House but then immediately gets killed by an assassin. Now bad guys are after Bobby as the only man who knows about a plot. Bishop must now not only survive, but to stop the conspirators from achieving their goal. And he doesn't know whom to trust.
Genres: [Action, Thriller]
Cast: Charlie Sheen as Bobby Bishop, Donald Sutherland as Jacob Conrad

ID: tt0117802
Title: Swingers (1996)
Summary: Cocktails first. Questions later.. This is a story about Mike, a guy who left his girl in New York when he came to LA to be a star. It's been six months since his girlfriend left him and he's not doing so good. So, his pal and some other friends try and get him back in the social scene and forget about his 6 year relationship.
Genres: [Comedy, Romance]
Cast: Heather Graham as Lorraine

ID: tt0039286
Title: Crossfire (1947)
Summary: Hate is like a loaded gun!. A man is murdered, apparently by one of a group of soldiers just out of the army. But which one? And why?
Genres: [Crime, Drama, Mystery, Thriller]
Cast: 

ID: tt0059243
Title: The Great Race (1965)
Summary: The movie with 20,000-mile or one-million-laughs guarantee!. Professional daredevil and white-suited hero, The Great Leslie, convinces turn-of-the-century auto makers that a race from New York to Paris (westward across America, the Bering Straight and Russia) will help to promote automobile sales. Leslie's arch-rival, the mustached and black-attired Professor Fate vows to beat Leslie to the finish line in a car of Fate's own invention.
Genres: [Adventure, Comedy]
Cast: Jack Lemmon as Prof. Fate, Keenan Wynn as Hezekiah Sturdy

ID: tt0048545
Title: Rebel Without a Cause (1955)
Summary: The bad boy from a good family.. After moving to a new town, troublemaking teen Jim Stark is supposed to have a clean slate, although being the new kid in town brings its own problems. While searching for some stability, Stark forms a bond with a disturbed classmate, Plato, and falls for local girl Judy. However, Judy is the girlfriend of neighborhood tough, Buzz. When Buzz violently confronts Jim and challenges him to a drag race, the new kid's real troubles begin.
Genres: [Drama]
Cast: Dennis Hopper as Goon

ID: tt0115580
Title: The Associate (1996)
Summary: Behind Every Great Man Is A Woman... Wishing He'd Get The Hell Out Of Her Way.. Laurel Ayres is a businesswoman trying to make it but unfortunately she works at a investment firm where she does all the work but all the senior investors like Frank Peterson grab all the credit. She then leaves and starts her own firm. While trying to find clients Laurel pretends that she has a male partner named Robert Cutty. And when she starts to do well all of her clients wants to meet Cutty which is difficult since he doesn't exist.
Genres: [Comedy]
Cast: Whoopi Goldberg as Laurel Ayres, Dianne Wiest as Sally Dugan, Bebe Neuwirth as Camille Scott, George Martin as Walter Manchester, Colleen Camp as Detective Jones, Allison Janney as Sandy

ID: tt0112907
Title: Drunks (1997)
Summary: All they want is another shot.... At the beginning of a nightly Alcoholics Anonymous meeting, Jim seems particularly troubled. His sponsor encourages him to talk that night, the first time in seven months, so he does - and leaves the meeting right after. As Jim wanders the night, searching for some solace in his old stomping grounds, bars and parks where he bought drugs, the meeting goes on, and we hear the stories of survivors and addicts - some, like Louis, who claim to have wandered in looking for choir practice, who don't call themselves alcoholic, and others, like Joseph, whose drinking almost caused the death of his child - as they talk about their lives at the meeting
Genres: [Drama]
Cast: Amanda Plummer as Shelley, Faye Dunaway as Becky, Parker Posey as Debbie, Dianne Wiest as Rachel, Kevin Corrigan as Cam, George Martin as Marty, Zach Grenier as Al, Anna Levine as Tanya (as Anna Thomson)

ID: tt0117318
Title: The People vs. Larry Flynt (1996)
Summary: You may not like what he does, but are you prepared to give up his right to do it?. Larry Flynt is the hedonistically obnoxious, but indomitable, publisher of Hustler magazine. The film recounts his struggle to make an honest living publishing his girlie magazine and how it changes into a battle to protect the freedom of speech for all people.
Genres: [Drama]
Cast: Woody Harrelson as Larry Flynt, James Cromwell as Charles Keating, Crispin Glover as Arlo, Vincent Schiavelli as Chester

ID: tt0116587
Title: Hustler White (1996)
Summary: See it with someone you've paid for.. Reminiscent of Sunset Boulevard, Hustler White transposes the action from the silver screen's old movie backlots to contemporary male prostitution and the porn industry. Said to be an homage to classic Hollywood cinema.
Genres: [Romance, Drama]
Cast: 

ID: tt0116212
Title: Entertaining Angels - The Dorothy Day Story (1996)
Summary: . "Entertaining Angels" is an interesting title for this movie about the 1920's and 30's social activist, Dorothy Day, for it can be regarded that what this woman did, sometimes single-handedly, always controversially, in her fight against social injustice would, indeed, be wonderful entertainment for angels... or it could mean that her work was for the benefit of the 'angels' at the bottom of the social ladder for whom she fought daily against those who would hold them down... this included her work as a suffregette. However it is meant, this film captures much of the real-life drama that took place on big city streets, and of the very personal trials which eventually led her to convert to roman Catholicism, and a dedication to helping the poor.
Genres: [Drama]
Cast: Martin Sheen as Peter Maurin, Heather Graham as Maggie Bowen

ID: tt0117677
Title: Snowriders (1996)
Summary: Man Wasn't Meant To Fly... Until Now!. Spectacular shots of snowcapped peaks and extreme skiers gracefully defying gravity, not to mention common sense, are the highlights of this entertaining video from the venerable guru of skiing movies, Warren Miller. Sparing no expense, Miller's film crews span the globe, shooting snowboarders careening down absurdly steep Alaskan peaks, ski guides helicoptering to remote slopes in British Columbia, and hardy (and uninhibited) Scotsmen who boldly catch some "big air" in kilts. This video is fraught with offbeat humor, including shots of a snowshoe race that could have been filmed by Mack Sennett, and footage of "face jumping," the oddball diversion of hurling oneself off Alpine glaciers and parachuting to earth. Miller's gentle narration, replete with trademark corny remarks reminiscent of old movie newsreels, mixes with a musical soundtrack of contemporary rock that is often uncannily synched with the astounding footage of extreme skiing.
Genres: [Documentary]
Cast: 

ID: tt0118147
Title: When We Were Kings (1996)
Summary: The untold story of the Rumble in the Jungle.. It's 1974. Muhammad Ali is 32 and thought by many to be past his prime. George Foreman is ten years younger and the heavyweight champion of the world. Promoter Don King wants to make a name for himself and offers both fighters five million dollars apiece to fight one another, and when they accept, King has only to come up with the money. He finds a willing backer in Mobutu Sese Suko, the dictator of Zaire, and the "Rumble in the Jungle" is set, including a musical festival featuring some of America's top black performers, like James Brown and B.B. King.
Genres: [Documentary]
Cast: 

ID: tt0110173
Title: JLG/JLG: Self-Portrait in December (1995)
Summary: . Director Jean-Luc Godard reflects in this movie about his place in film history, the interaction of film industry and film as art, as well as the act of creating art.
Genres: [Documentary]
Cast: 

ID: tt0040723
Title: Raw Deal (1948)
Summary: Bullets!  Women! -- Can't Hold a Man Like That!. In this film noir classic, a revenge-seeking gangster (Dennis O'Keefe) is sent to prison after being framed for a crime he didn't commit. After seducing a beautiful young woman, he uses her to help him carry out his plot for vengeance, leading him to the crazy pyromaniac (Raymond Burr) who set him up.
Genres: [Crime]
Cast: 

ID: tt0115610
Title: Bad Moon (1996)
Summary: Half man.  Half wolf.  Total terror.. One man's struggle to contain the curse he hides within... and his last-ditch attempt to free himself with the love of family. But when it looks as if he is losing his battle, and endangering all he holds most dear, the family dog, Thor, is the last hope for his family's survival... and the end to his Werewolf curse.
Genres: [Horror]
Cast: 

ID: tt0112483
Title: Best of the Best 3: No Turning Back (1995)
Summary: . Tommy Lee (Phillip Rhee) comes to visit his sister and finds that the town is being overrun with members of the Arian brotherhood. These Arian brothers certainly weren't expecting a Taekwondo expert to show up. Tommy and his sister's husband played by Christopher Mcdonald team up to stop the racism. whats better than watching neo-nazis get the crap kicked out of them? when the person kicking the crap out of them is dressed like homie the killer clown, complete with big red shoes and a foam nose. and thats just ONE of the MANY many many many many many cinematic jewels contained within this action packed blessing to the silver screen.this is the best worst martial arts film since sonny chiba's street fighter! you don't believe me? see for yourself namby-pants and check out the best of the best 3. now!!!
Genres: [Action]
Cast: Christopher McDonald as Sheriff Jack Banning, Gina Gershon as Margo Preston, Mark Rolston as Donnie Hansen

ID: tt0103850
Title: Bob Roberts (1992)
Summary: Vote first.  Ask questions later.. Mock documentary about an upstart candidate for the U.S. Senate written and directed by actor Tim Robbins. Bob Roberts is a folksinger with a difference: He offers tunes that protest welfare chiselers, liberal whining, and the like. As the filmmakers follow his campaign, Robbins gives needle-sharp insight into the way candidates manipulate the media.
Genres: [Comedy, Drama]
Cast: Tim Robbins as Bob Roberts, Giancarlo Esposito as Bugs Raplin, David Strathairn as Mack Laflin, James Spader as Chuck Marlin, Jack Black as Roger Davis, Susan Sarandon as Tawna Titan, John Cusack as Cutting Edge Host, Jeremy Piven as Candle Seller, Peter Gallagher as Dan Riley, Bob Balaban as Michael Janes

ID: tt0095765
Title: Cinema Paradiso (1988)
Summary: A celebration of youth, friendship, and the everlasting magic of the movies.. A filmmaker recalls his childhood, when he fell in love with the movies at his village's theater and formed a deep friendship with the theater's projectionist.
Genres: [Drama, Romance]
Cast: 

ID: tt0109942
Title: Dead Tired (1994)
Summary: . Stressed and overworked, famous French movie star Michel Blanc (Michel Blanc) is beginning to wear down, physically and mentally, from the pressure and demands of fame. Already in a fragile state of mind, strange events start to transpire all around him, and he gradually loses his grip. Taking the advice of a psychiatrist, Blanc retreats to the countryside with his friend, Carole Bouquet (Carole Bouquet), but Blanc still has not managed to escape all of his problems.
Genres: [Comedy]
Cast: 

ID: tt0102426
Title: Mediterraneo (1991)
Summary: On a magical Greek island a soldier is about to discover that it is better to make love instead of war. Greek Sea, World War II. An Italian ship leaves a handful of soldiers in a little island; their mission is to spot enemy ships and to hold the island in case of attack. The village of the island seems abandoned and there isn't a single enemy in sight, so the soldiers begin to relax a little. Things change when their ship is hit and destroyed by the enemy, and the soldiers find themselves abandoned there. Actually, the island isn't deserted and when the Greeks understand that those Italians are harmless, they came out of their hiding places in the mountains and continue their peaceful lives. Soon the soldiers discover that being left behind in a God-forgotten Greek island isn't such a bad thing, after all...
Genres: [Comedy, Romance, War]
Cast: 

ID: tt0105107
Title: Passion Fish (1992)
Summary: . Soap actress May-Alice Culhane is paralyzed from the waist down in an accident. She returns to her old home in Louisiana, where she proceeds to drink quite steadily and to drive away five personal attendants in the first twenty minutes of the movie. Then she meets with Chantelle, whose stubbornness matches her own -- if only because Chantelle, herself a recovering cocaine addict, cannot afford to lose this job. Their mutual dislike gradually develops into an armed truce as the two women deal with their own problems and with each others'.
Genres: [Drama]
Cast: Mary McDonnell as May-Alice Culhane, Alfre Woodard as Chantelle, Angela Bassett as Rhonda / Dawn, David Strathairn as Rennie

ID: tt0096257
Title: The Thin Blue Line (1988)
Summary: A softcore movie, Dr. Death, a chocolate milkshake, a nosey blonde and "The Carol Burnett Show." Solving this mystery is going to be murder.. Errol Morris's unique documentary dramatically re-enacts the crime scene and investigation of a police officer's murder in Dallas.
Genres: [Mystery, Documentary, Crime]
Cast: 

ID: tt0078446
Title: Up in Smoke (1978)
Summary: The movie that'll have you rolling in the aisles.. An unemployed pot-smoking slacker and amateur drummer, Anthony Stoner ditches his strict parents and hits the road, eventually meeting kindred spirit Pedro de Pacas. While the drug-ingesting duo is soon arrested for possession of marijuana, Anthony and Pedro get released on a technicality, allowing them to continue their many misadventures and ultimately compete in a rock band contest, where they perform the raucous tune "Earache My Eye."
Genres: [Comedy]
Cast: Cheech Marin as Pedro De Pacas, Tom Skerritt as Strawberry

ID: tt0060196
Title: The Good, the Bad and the Ugly (1966)
Summary: For three men the Civil War wasn't hell. It was practice.. While the Civil War rages between the Union and the Confederacy, three men – a quiet loner, a ruthless hit man and a Mexican bandit – comb the American Southwest in search of a strongbox containing $200,000 in stolen gold.
Genres: [Western]
Cast: Clint Eastwood as Blondie, Philip Ettington as Pedro (uncredited)

ID: tt0094336
Title: Withnail & I (1987)
Summary: If you don't remember the sixties, don't worry - neither did they.. Two 'resting' actors living in a squalid Camden Flat - and living off a diet of booze and pills - take a trip to a country house (belonging to Withnail’s uncle) to 'rejuvenate'. Faced with bad weather, altercations with the locals, and the unexpected arrival (and advances) of Uncle Monty, the pairs wits and friendship are tested...  Set in 1969, the year in which the hippy dreams of so many young Englishmen went sour, 1986's Bruce Robinson's Withnail and I is an enduring British cult. Withnail is played by the emaciated but defiantly effete Richard E Grant, "I" (i.e., Marwood) by Paul McGann. Out-of-work actors living in desperate penury in a rancid London flat, their lives are a continual struggle to keep warm, alive and in Marwood's case sane, until the pubs open.  A sojourn in the country cottage of Withnail's Uncle Monty only redoubles their privations.
Genres: [Comedy, Drama]
Cast: Richard E. Grant as Withnail

ID: tt0066921
Title: A Clockwork Orange (1971)
Summary: Being the adventures of a young man whose principal interests are rape, ultra-violence and Beethoven.. Demonic gang-leader Alex goes on the spree of rape, mugging and murder with his pack of "droogs". But he's a boy who also likes Beethoven's Ninth and a bit of "the old in-out, in-out". He later finds himself at the mercy of the state and its brainwashing experiment designed to take violence off the streets.
Genres: [Science Fiction, Drama]
Cast: Malcolm McDowell as Alexander  DeLarge

ID: tt0056592
Title: To Kill a Mockingbird (1962)
Summary: . In a small Alabama town in the 1930s, scrupulously honest and highly respected lawyer, Atticus Finch puts his career on the line when he agrees to represent Tom Robinson, a black man accused of rape. The trial and the events surrounding it are seen through the eyes of Finch's six-year-old daughter, Scout. While Robinson's trial gives the movie its momentum, there are plenty of anecdotal occurrences before and after the court date: Scout's ever-strengthening bond with older brother, Jem, her friendship with precocious young Dill Harris, her father's no-nonsense reactions to such life-and-death crises as a rampaging mad dog, and especially Scout's reactions to, and relationship with, Boo Radley, the reclusive 'village idiot' who turns out to be her salvation when she is attacked by a venomous bigot.
Genres: [Crime, Drama]
Cast: Robert Duvall as Arthur 'Boo' Radley, Arthur Tovey as Juror (uncredited)

ID: tt0095250
Title: The Big Blue (1988)
Summary: Danger...Like Passion...Runs Deep. Two men answer the call of the ocean in this romantic fantasy-adventure.  Jacques (Jean-Marc Barr) and Enzo (Jean Reno) are a pair of friends who have been close since childhood, and who share a passion for the dangerous sport of free diving. Professional diver Jacques opted to follow in the footsteps of his father, who died at sea when Jacques was a boy; to the bewilderment of scientists, Jacques harbors a remarkable ability to adjust his heart rate and breathing pattern in the water, so that his vital signs more closely resemble that of dolphins than men (he even considers a school of dolphins as his extended family).  As Enzo persuades a reluctant Jacques to compete against him in a free diving contest -- determining who can dive deeper and longer without scuba gear -- Jacques meets Johana (Rosanna Arquette), a beautiful insurance investigator from America, and he finds that he must choose between his love for her and his love of the sea.
Genres: [Adventure, Drama, Romance]
Cast: Jean Reno as Enzo Molinari

ID: tt0092610
Title: Bad Taste (1987)
Summary: . A team from the intergalactic fast food chain Crumb's Crunchy Delights descends on Earth, planning to make human flesh the newest taste sensation. After they wipe out the New Zealand town Kaihoro, the country's Astro-Investigation and Defense Service is called in to deal with the problem. Things are complicated due to Giles, an aid worker who comes to Kaihoro the same day to collect change from the residents. He is captured by the aliens, and AIaDS stages a rescue mission that quickly becomes an all-out assault on the aliens' headquarters.
Genres: [Action, Comedy, Horror, Science Fiction]
Cast: 

ID: tt0056218
Title: The Manchurian Candidate (1962)
Summary: When you've seen it all, you'll swear there's never been anything like it!. The Manchurian Candidate is a political thriller from American director John Frankenheimer. An American soldier is brainwashed into being a killer for the communist Russians during the Korean War.
Genres: [Mystery, Thriller]
Cast: Bess Flowers as Woman in Dream Sequence

ID: tt0052618
Title: Ben-Hur (1959)
Summary: The entertainment experience of a lifetime.. Ben-Hur is a 1959 epic film directed by William Wyler, the third film version of Civil War vet Lew Wallace's 1880 novel Ben-Hur: A Tale of the Christ. It premiered at Loew's State Theatre in New York City on November 18, 1959. The movie's reputation as a classic is primarily based on two spectacular action sequences:  the great chariot race and a Roman naval battle, along with lavish production values and strong performances.  The plot of Ben Hur revolves around a Jewish prince who is betrayed and sent into slavery by a Roman friend and how he regains his freedom and comes back for revenge. However, instead he finds redemption in Christ, the theme is ultimately about being saved in the Christian sense.  The film went on to win a record of eleven Academy Awards, including Best Picture and Best Actor (Charlton Heston as Ben Hur). This record-setting Oscars sweep has since been equaled by Titanic in 1998 and The Lord of the Rings: The Return of the King in 2004, but never broken.
Genres: [Action, Adventure, Drama, History]
Cast: Charlton Heston as Judah Ben-Hur

ID: tt0084503
Title: Pink Floyd: The Wall (1982)
Summary: The Memories. The Madness. The Music... The Movie.. A troubled rock star descends into madness in the midst of his physical and social isolation from everyone.
Genres: [Music]
Cast: Bob Hoskins as Rock and Roll Manager

ID: tt0101458
Title: Until the End of the World (1991)
Summary: . . . the ultimate road movie. Until The End of the World is an odyssey for the modern age. As with Homer's Odyssey, the purpose of the journey is to restore sight -- a spiritual reconciliation between an obsessed father and a deserted son. Dr. Farber, in trying to find a cure for his wife's blindness, has created a device that allows the user to send images directly to the brain, enabling the blind to see.
Genres: [Thriller, Drama, Science Fiction, Action]
Cast: William Hurt as Sam Farber, alias Trevor McPhee, Sam Neill as Eugene Fitzpatrick, Max von Sydow as Henry Farber

ID: tt0116589
Title: Hype! (1996)
Summary: . This documentary examines the Seattle scene as it became the focus of a merging of punk rock, heavy metal, and innovation. Building from the grass roots, self-promoted and self-recorded until break-out success of bands like Nirvana brought the record industry to the Pacific Northwest, a phenomenon was born.
Genres: [Documentary, Music]
Cast: 

ID: tt0085159
Title: Amityville 3-D (1983)
Summary: WARNING: in this movie you are the victim.. To debunk the Amityville house's infamous reputation and take advantage of a rock-bottom asking price, skeptical journalist John Baxter buys the place and settles in to write his first novel. But as soon as the ink on the deed has dried, people who have come into contact with John and the house begin to meet with shocking fates. Is it all just coincidence, or is the house really the gateway to hell?
Genres: [Horror, Thriller]
Cast: Meg Ryan as Lisa

ID: tt0087800
Title: A Nightmare on Elm Street (1984)
Summary: If Nancy Doesn't Wake Up Screaming, She Won't Wake Up At All!. Teenagers in a small town are dropping like flies, apparently in the grip of mass hysteria causing their suicides. A cop's daughter, Nancy Thompson (Heather Langenkamp) traces the cause to child molester Fred Krueger (Robert Englund), who was burned alive by angry parents many years before. Krueger has now come back in the dreams of his killers' children, claiming their lives as his revenge. Nancy and her boyfriend, Glen (Johnny Depp), must devise a plan to lure the monster out of the realm of nightmares and into the real world...
Genres: [Horror]
Cast: Johnny Depp as Glen Lantz, Robert Englund as Freddy Krueger, Charles Fleischer as Dr. King

ID: tt0115495
Title: Albino Alligator (1996)
Summary: They Planned The Perfect Crime... Until It All Went Perfectly Wrong!. Three petty thieves who the police believe to be major criminals are chased into a basement bar where they take five hostages including all the bar employees. The rest of the movie deals with the cops lurking outside the bar while the trio try to get hold of the situation inside.
Genres: [Crime, Drama, Thriller]
Cast: Matt Dillon as Dova, Faye Dunaway as Janet Boudreaux, Viggo Mortensen as Guy Foucard, Skeet Ulrich as Danny Boudreaux, Frankie Faison as Agent Marv Rose, M. Emmet Walsh as Dino, Joe Mantegna as A.T.F. Agent G.D. Browning

ID: tt0084116
Title: Identification of a Woman (1982)
Summary: . The movie director Niccolò has just been left by his wife. Subsequently he embarks on an obsessive relationship with a young woman who eventually leaves him and disappears (shades also of L'Avventura) while searching for her, he meets a variety of other willing girls. This gives him the idea of making a movie about women's relationships. He starts to search for a woman who can play the leading part in the movie.
Genres: [Romance, Drama]
Cast: 

ID: tt0117372
Title: The Preacher's Wife (1996)
Summary: They needed help. What they got was a miracle.. Good natured Reverend Henry Biggs finds that his marriage to choir mistress Julia is flagging, due to his constant absence caring for the deprived neighborhood they live in. On top of all this, his church is coming under threat from property developer Joe Hamilton. In desperation, Rev. Biggs prays to God for help - and help arrives in the form of an angel named Dudley.
Genres: [Fantasy, Drama, Comedy, Science Fiction, Romance, Family]
Cast: Denzel Washington as Dudley

ID: tt0113557
Title: Zero Kelvin (1995)
Summary: . Larsen, an aspiring poet in '20s Oslo, leaves his girlfriend to spend a year as a trapper in East Greenland. There he is teamed with a seemingly rough old sailor/trapper, Randbæk, and a scientist, Holm. Trapped in a tiny hut together as the Arctic winter sets in, a complex and intense love/hate relationship develops between Randbæk and Larsen, who are more similar than either would like to admit. A powerful psychological and physical drama set against stunningly bleak Arctic scenery.
Genres: [Drama, Thriller]
Cast: Stellan Skarsgård as Randbæk

ID: tt0115988
Title: The Crucible (1996)
Summary: Arthur Miller's timeless tale of truth on trial.. The movie is centered around the Salem Massachusetts witch trials of 1692. The movie is based on the play "The Crucible" by Arthur Miller. He also wrote the screen play adaptation.
Genres: [Drama, History]
Cast: Daniel Day-Lewis as John Proctor, Winona Ryder as Abigail Williams, Bruce Davison as Reverend Parris, Jeffrey Jones as Thomas Putnam, Peter Vaughan as Giles Corey

ID: tt0042354
Title: The Forbidden Christ (1951)
Summary: . Strange Deception combines a standard revenge tale with a postwar reenactment of the first four books of the New Testament. Freshly released from a Russian POW camp, Italian soldier Raf Vallone tries to discover who betrayed his brother to the Nazis. Alain Cuny is an enigmatic carpenter who has confessed to causing the brother's death. Cuny is slain by Vallone, whereupon it is revealed that the carpenter sacrificed himself on behalf of the real culprit, Phillipe Lemaire. Vallone catches up with Lemaire, but is unable to kill him, thanks to the Christlike example of Cuny. Originally titled Il Cristo Proibito (The Forbidden Christ, just so we don't miss the point), this film represented the movie directorial debut of novelist Curzio Malaparte, who also wrote the musical score.
Genres: [Drama]
Cast: 

ID: tt0077631
Title: Grease (1978)
Summary: Grease is the word. Australian good girl Sandy and greaser Danny fell in love over the summer. But when they unexpectedly discover they're now in the same high school, will they be able to rekindle their romance despite their eccentric friends?
Genres: [Romance]
Cast: John Travolta as Danny Zuko, Stockard Channing as Betty Rizzo

ID: tt0100114
Title: Marked for Death (1990)
Summary: He's a good cop. In a bad mood.. Just retired from the Drug Enforcement Agency, John Hatcher (Seagal) returns to his hometown and quickly discovers that drugs have infiltrated his old neighborhood. Determined to drive the dealers out, Hatcher crosses pathes with a ferocious Jamaican druglord who vowes that Hatcher and his family are now marked for death.
Genres: [Action, Thriller]
Cast: Keith David as Max, Kevin Dunn as Sal, Danny Trejo as Hector

ID: tt0116695
Title: Jerry Maguire (1996)
Summary: Everybody loved him... Everybody disappeared.. Jerry Maguire used to be a typical sports agent: willing to do just about anything he could to get the biggest possible contracts for his clients, plus a nice commission for himself. Then, one day, he suddenly has second thoughts about what he's really doing. When he voices these doubts, he ends up losing his job and all of his clients, save Rod Tidwell, an egomaniacal football player.
Genres: [Comedy, Drama, Romance]
Cast: Tom Cruise as Jerry Maguire, Cuba Gooding Jr. as Rod Tidwell, Renée Zellweger as Dorothy Boyd, Kelly Preston as Avery Bishop, Bonnie Hunt as Laurel Boyd, Donal Logue as Rick, Eric Stoltz as Ethan Valhere

ID: tt0115633
Title: Bastard Out of Carolina (1996)
Summary: . Difficult tale of poor, struggling South Carolinian mother &amp; daughter, who each face painful choices with their resolve and pride. Bone, the eldest daughter, and Anney her tired mother, grow both closer and farther apart: Anney sees Glen as her last chance.  The film won an Emmy Award for "Outstanding Casting for a Miniseries or a Special" and was nominated for "Outstanding Directing for a Miniseries or a Special", "Outstanding Supporting Actress in a Miniseries or a Special", and "Outstanding Made for Television Movie". It was screened in the Un Certain Regard section at the 1996 Cannes Film Festival.
Genres: [Drama]
Cast: Jennifer Jason Leigh as Anney Boatwright, Christina Ricci as Dee Dee

ID: tt0116410
Title: Ghosts of Mississippi (1996)
Summary: From the director of A FEW GOOD MEN comes a compelling TRUE STORY.. Ghosts of Mississippi is a drama covering the final trial of the assassin, Bryon De La Beckwith, of the 60s civil rights leader Medgar Evers. It begins with the murder and the events surrounding the two initial trials which both ended in a hung jury. The movie then covers District Attorney, Bobby DeLaughters transformation and alliance with Myrlie Evers, wife of Medgar Evers, of the, as he becomes more involved with bringing Beckwith to trial for the third time 30 years later. Some of the characters are played by the actual participants in this story.
Genres: [History, Drama]
Cast: Alec Baldwin as Bobby DeLaughter, James Woods as Byron De La Beckwith, Whoopi Goldberg as Myrlie Evers, William H. Macy as Charlie Crisco

ID: tt0117571
Title: Scream (1996)
Summary: Someone has taken their love of scary movies one step too far.. A killer known as Ghostface begins killing off teenagers, and as the body count begins rising, one girl and her friends find themselves contemplating the 'rules' of horror films as they find themselves living in a real-life one.
Genres: [Crime, Horror, Mystery]
Cast: David Arquette as Deputy Dwight 'Dewey' Riley, Matthew Lillard as Stu Macher, Skeet Ulrich as Billy Loomis, Drew Barrymore as Casey Becker, Liev Schreiber as Cotton Weary

ID: tt0116640
Title: Inside (1996)
Summary: . An emotional and thrilling tale of life and death behind the walls of a maximum security prison. From the acclaimed director of 'Bonnie and Clyde', and featuring a powerful international cast.
Genres: [Crime, Drama, TV Movie]
Cast: Nigel Hawthorne as Colonel, Eric Stoltz as Marty, Louis Gossett, Jr. as Questioner

ID: tt0080714
Title: Falling in Love Again (1980)
Summary: . Harry and Sue Lewis met in the 40s as teenagers living in the Bronx. He was an aspiring architect, she was the most beautiful girl in school, and both had a fondness for bran muffins. They fell in love, got married, moved to Los Angeles, and had two kids. While struggling with his midlife crisis, Harry receives an invitation for his high school's reunion back so he takes Sue and their teenage kids on a cross-country car trip back to the Big Apple. Will they see in the Bronx what they expected? Will the good memories from their past help rekindle their fading love? Is it too late to dream?
Genres: [Drama, Romance]
Cast: Elliott Gould as Harry Lewis (Present day), Michelle Pfeiffer as Sue Wellington (1940's)

ID: tt0106266
Title: Amos & Andrew (1993)
Summary: Misunderstood. Misplaced. Mismatched.. When Andrew Sterling (Samuel L. Jackson), a successful black urbanite writer, buys a vacation home on a resort in New England the police mistake him for a burglar. After surrounding his home with armed men, Chief Tolliver (Dabney Coleman) realizes his mistake and to avoid the bad publicity offers a thief in his jail, Amos Odell (Nicolas Cage) a deal.
Genres: [Action, Comedy]
Cast: Samuel L. Jackson as Andrew Sterling, Michael Lerner as Phil Gillman, Nicolas Cage as Amos Odell, Brad Dourif as Officer Donnie Donaldson

ID: tt0118111
Title: Waiting for Guffman (1996)
Summary: There's a good reason some talent remains undiscovered.. Corky St. Clair is a director, actor and dancer in Blaine, Missouri. When it comes time to celebrate Blaine's 150th anniversary, Corky resolves to bring down the house in Broadway style in this hilarious mockumentary from the people who brought you "This is Spinal Tap!"
Genres: [Music, Comedy]
Cast: Eugene Levy as Dr. Allan Pearl, Catherine O'Hara as Sheila Albertson, Larry Miller as Glenn Welsch, Parker Posey as Libby Mae Brown, Bob Balaban as Lloyd Miller, Paul Dooley as UFO Abductee, Brian Doyle-Murray as Red Savage

ID: tt0119951
Title: Private Parts (1997)
Summary: Never before has a man done so much with so little.. The auto-biographical story of Howard Stern, the radio-rebel who is now also a TV-personality, an author and a movie star.
Genres: [Comedy, Drama]
Cast: Paul Giamatti as Kenny 'Pig Vomit' Rushton, Allison Janney as Dee Dee, Mia Farrow as Herself

ID: tt0118064
Title: The Van (1996)
Summary: A comedy About Friends, Family and Fish.. Set in "Barrytown", a fictitious working-class quarter of Dublin. "Bimbo" Reeves gets laid off from his job. With his redundancy, he buys a van and sells fish and chips with his buddy Larry. Due to Ireland's surprising success at the 1990 FIFA World Cup, their business starts off well, but the relationship between the two friends soon becomes strained as Bimbo behaves more like a typical boss.
Genres: [Comedy, Drama]
Cast: Colm Meaney as Larry

ID: tt0118829
Title: Cats Don't Dance (1997)
Summary: It's a Jungle Out There!. Danny, an ambitious singing/dancing cat, goes to Hollywood and overcomes several obstacles to fulfill his dream of becoming a movie star.
Genres: [Animation, Comedy, Family, Music]
Cast: 

ID: tt0120389
Title: Turbo: A Power Rangers Movie (1997)
Summary: The Power Is Back!. The legendary Power Rangers must stop the evil space pirate Divatox from releasing the powerful Maligore from his volcanic imprisonment on the island of Muranthias, where only the kindly wizard Lerigot has the key to release him. The hope of victory lies in the Ranger's incredible new Turbo powers and powerful Turbo Zords.
Genres: [Action, Adventure, Family, Fantasy, Science Fiction, Thriller]
Cast: 

ID: tt0116762
Title: Keys to Tulsa (1997)
Summary: Two Men Taken By One Woman. Richter Boudreau is on a bad streak: Languishing in the shadow of his celebrity mother, he loses his job as a film critic for the town paper, and now he's been approached with a dangerous proposition that ultimately leads to blackmail. Richter's friend Ronnie ropes him into a scheme to steal the inheritance of his wife, Vicky.
Genres: [Crime, Drama, Thriller]
Cast: Eric Stoltz as Richter Boudreau, James Spader as Ronnie Stover, Michael Rooker as Keith Michaels, James Coburn as Harmon Shaw, Cameron Diaz as Trudy

ID: tt0120087
Title: The Second Jungle Book: Mowgli & Baloo (1997)
Summary: . In this exciting live-action adventure, young Mowgli, an orphan raised by wolves, is spotted by a scout for a giant circus. Accompanied by a cruel hunter and a snake charmer, the scout sets out to trap Mowgli. But with the help of Baloo the bear and Bagheera the panther, little Mowgli leads the adults into his biggest and wildest adventure yet! A fun-filled movie every member of the family will enjoy.
Genres: [Adventure, Family]
Cast: Roddy McDowall as King Murphy

ID: tt0118127
Title: Wedding Bell Blues (1996)
Summary: . Micki, Tanya and Jasmine have 24 hours to get divorced. There's just one problem...They're still single.  When Jasmine, Tanya and Micki decide they'd rather be divorced than unmarried, they take off to Las Vegas in search of the perfect ex. In the tangled web that follows, Micki lassos a cowboy, Tanya acquires a rich businessman, and Jasmine finds true love in the arms of a regular guy. The result is an outrageous comedy of love, marriage and misadventure.  As intelligent as it is absurd, Wedding Bell Blues pokes great fun at the twists and turns of modern love, with outstanding performances from a talented young cast, including Illeana Douglas, Paulina Porizkova and Julie Warner, it is without a doubt one of the year's most hilarious movies.
Genres: [Comedy, Romance]
Cast: Illeana Douglas as Jasmine, Richard Edson as Tom

ID: tt0120034
Title: Roseanna's Grave (1997)
Summary: A romantic comedy about the things we do for love.. Roseanna is dying of a heart condition, and all she wants is to be buried next to her daughter, in a cemetery that is getting full fast. The cemetery can't expand because Capestro, the man who owns the land next to the cemetery, won't sell. While Marcello is doing good deeds to make sure no one dies, Roseanna thinks of Marcello's future.
Genres: [Comedy, Romance, Drama]
Cast: Jean Reno as Marcello

ID: tt0105569
Title: Tetsuo II: Body Hammer (1992)
Summary: . Tetsuo II: Body Hammer is a 1992 Japanese science fiction/horror film directed by Shinya Tsukamoto. It is a bigger-budget reworking of the same director's 1989 movie Tetsuo: The Iron Man, utilizing similar themes and ideas to the earlier film (a Japanese salaryman, played by cult actor Tomorowo Taguchi, finds his body transforming into a weapon through sheer rage after his son is kidnapped by a gang of violent thugs.) It was not as well received as its predecessor but it did win the Critic's Award at the 3rd Yubari International Fantastic Film Festival in February 1992.
Genres: [Science Fiction, Horror, Drama, Thriller]
Cast: 

ID: tt0119094
Title: Face/Off (1997)
Summary: In order to catch him, he must become him.. An antiterrorism agent goes under the knife to acquire the likeness of a terrorist and gather details about a bombing plot. When the terrorist escapes custody, he undergoes surgery to look like the agent so he can get close to the agent's family.
Genres: [Action, Crime, Science Fiction, Thriller]
Cast: John Travolta as Sean Archer, Nicolas Cage as Castor Troy, Gina Gershon as Sasha Hassler, John Carroll Lynch as Walt

ID: tt0116334
Title: For Ever Mozart (1996)
Summary: . For Ever Mozart is an episodic film that follows a theater troupe from France attempting to put on a play in Sarajevo. Along their journey they are captured and held in a POW camp, and they call for help from their friends and relations in France. Director Jean-Luc Godard presents stories about this troop to ask how one can make art while slaughters like the one in Bosnia are taking place, and he throws in a strong critique of the European Union. For Ever Mozart is one of Godard's most disjointed and difficult films. Its stories sometimes seem to form a whole and at other times the links among them are unclear. One gets the impression that in each episode Godard attempts to start a film only to come to the conclusion that it is impossible to continue. It features some of the most beautiful shots of tanks in the cinema.
Genres: [Drama, Comedy, War]
Cast: 

ID: tt0118884
Title: Contact (1997)
Summary: If it's just us, it seems like an awful waste of space.. Contact is a science fiction film about an encounter with alien intelligence. Based on the novel by Carl Sagan the film starred Jodie Foster as the one chosen scientist who must make some difficult decisions between her beliefs, the truth, and reality.
Genres: [Drama, Science Fiction, Mystery]
Cast: Jodie Foster as Eleanor Arroway, Matthew McConaughey as Palmer Joss, James Woods as Michael Kitz, John Hurt as S. R. Hadden, Tom Skerritt as David Drumlin, David Morse as Ted Arroway, Angela Bassett as Rachel Constantine

ID: tt0118887
Title: Cop Land (1997)
Summary: No One Is Above The Law.. Freddy Heflin is the sheriff of a place everyone calls “Cop Land” — a small and seemingly peaceful town populated by the big city police officers he’s long admired. Yet something ugly is taking place behind the town’s peaceful facade. And when Freddy uncovers a massive, deadly conspiracy among these local residents, he is forced to take action and make a dangerous choice between protecting his idols and upholding the law.
Genres: [Action, Crime, Drama]
Cast: Sylvester Stallone as Sheriff Freddy Heflin, Harvey Keitel as Ray Donlan, Ray Liotta as Gary " Figgsy " Figgis, Robert De Niro as Lt. Moe Tilden, Michael Rapaport as Murray " Superboy " Babitch, Annabella Sciorra as Liz Randone, Janeane Garofalo as Deputy Cindy Betts, Cathy Moriarty as Rose Donlan, Paul Calderon as Hector - Medic, Philip Ettington as Detective Strickland (uncredited)

ID: tt0118570
Title: Air Bud (1997)
Summary: He Sits. He Stays. He Shoots. He Scores.. A young boy and a talented stray dog with an amazing basketball playing ability become instant friends. Rebounding from his father's accidental death, 12-year-old Josh Framm moves with his family to the small town of Fernfield, Washington. The new kid in town, Josh has no friends and is too shy to try out for the school basketball team. Instead he prefers to practice alone on an abandoned court, he befriends a runaway golden retriever named Buddy. Josh is amazed when he realizes that Buddy loves basketball...that is playing basketball...and he is GOOD! Josh eventually makes the school team and Buddy is named the Team Mascot. Josh and Buddy become the stars of halftime. Buddy's half-time talent draws media attention. Unfortunately, when Buddy's mean former owner, Norm Snively, comes along with a scheme to cash in on the pup's celebrity, it looks like they are going to be separated.
Genres: [Comedy, Family, Drama]
Cast: Michael Jeter as Norm Snively, Bill Cobbs as Arthur Chaney

ID: tt0119509
Title: Leave it to Beaver (1997)
Summary: The Beav is back.. Cleavers are an all-American family living in Ohio - wise father Ward, loving mother June, teen-age son Wally and 8-year-old "Beaver" Theodore. Beaver hopes to get a bike as a gift from his father and to please him tries out for his school football team and he makes it, only to be embarrassed. The bike he gets is quickly stolen. Meanwhile Wally is trying to help his friend Eddie Haskell to get the heart of pretty classmate Karen, but Karen seems to like Wally more, and that leads to tensions between the friends.
Genres: [Adventure, Comedy, Drama, Family]
Cast: Christopher McDonald as Ward Cleaver

ID: tt0119695
Title: Money Talks (1997)
Summary: This ain't no buddy movie.. Money Talks is a 1997 American comedy film directed by Brett Ratner. Sought by police and criminals, a small-time huckster makes a deal with a TV newsman for protection.
Genres: [Action, Adventure, Comedy]
Cast: Charlie Sheen as James Russell, Paul Sorvino as Guy Cipriani

ID: tt0119457
Title: Kicked in the Head (1997)
Summary: A satirical love story.. Redmond is a young guy who can't find what to do with his life. When his uncle Sam gives him the bag to deliver to some uptown connection he fails to do so and it gets them in trouble with Jack, low-key criminal. After that tough guy Stretch wants Redmond to take part in his illegal beer business, but before Redmond gets involved, the business ends in a bad way. Redmond is also having affair with flight attendant Megan.
Genres: [Comedy, Crime, Drama, Romance]
Cast: Kevin Corrigan as Redmond, Michael Rapaport as Stretch, Lili Taylor as Happy, James Woods as Uncle Sam, Burt Young as Jack

ID: tt0120151
Title: A Smile Like Yours (1997)
Summary: The shortest distance between two people is a certain smile.. A comedy about a couple who cannot conceive a baby
Genres: [Comedy, Romance]
Cast: Joan Cusack as Nancy Tellen, Christopher McDonald as Richard Halstrom, Donald Moffat as Dr. Felber, Shirley MacLaine as Martha (uncredited)

ID: tt0118842
Title: Chasing Amy (1997)
Summary: It's not who you love. It's how.. Holden and Banky are comic book artists. Everything is going good for them until they meet Alyssa, also a comic book artist. Holden falls for her, but his hopes are crushed when he finds out she's a lesbian.
Genres: [Comedy, Drama, Romance]
Cast: Ben Affleck as Holden McNeil, Joey Lauren Adams as Alyssa Jones, Matt Damon as Shawn Oran, Illeana Douglas as Alyssa's Roommate (uncredited)

ID: tt0119280
Title: Mrs Brown (1997)
Summary: Loyalty without question. Friendship without equal.. Queen Victoria is deeply depressed after the death of her husband, disappearing from public. Her servant Brown, who adores her, through caress and admiration brings her back to life, but that relationship creates scandalous situation and is likely to lead to monarchy crisis.
Genres: [Drama, History, Romance]
Cast: Judi Dench as Queen Victoria

ID: tt0119107
Title: Fast, Cheap & Out of Control (1997)
Summary: . Hilarious, sad, absurd, eerie and beautiful, "FAST, CHEAP &amp; OUT OF CONTROL" is a film like no other. Starting as a darkly funny contemplation of the Sisyphus-like nature of human striving, it ultimately becomes a profoundly moving meditation on the very nature of existence.
Genres: [Documentary]
Cast: 

ID: tt0120539
Title: Year of the Horse (1997)
Summary: . Indie director Jim Jarmusch lenses a low-tech tribute to protean rocker Neil Young and his long-standing band, Crazy Horse. Stitched together from archival material shot in 1976 and 1986 along with candid scenes of Young and the band kicking back between shows, this rockumentary is as ragged as it is direct. Concert performances include renditions of hits such as "Sedan Delivery" and "Like a Hurricane."
Genres: [Documentary, Music]
Cast: 

ID: tt0120257
Title: Swept from the Sea (1998)
Summary: . The film tells the story of Russian emigree and the only survivor from ship crash Yanko Goorall and servant Amy Foster in the end of 19th century. When Yanko enters a farm sick and hungry after the shipwreck, everyone is afraid of him, except for Amy, who is very kind and helps him. Soon he becomes like a son for Dr. James Kennedy and romance between Yanko and Amy follows.
Genres: [Drama, Romance]
Cast: Ian McKellen as Dr. James Kennedy, Kathy Bates as Miss Swaffer, Joss Ackland as Mr. Swaffer

ID: tt0119832
Title: One Night Stand (1997)
Summary: Sometimes an entire life can change in just one night.. In Los Angeles, Max Carlyle makes a good living directing commercials and has a happy home life with his wife, Mimi, and two children. When Carlyle travels to New York City to visit his friend Charlie, who has been diagnosed with AIDS, he has repeat run-ins with a beautiful woman, Karen, and eventually sleeps with her. Though he goes home the next day and doesn't return until a year later, Carlyle's infidelity still lingers.
Genres: [Drama]
Cast: Wesley Snipes as Max Carlyle, Nastassja Kinski as Karen, Robert Downey Jr. as Charlie

ID: tt0120201
Title: Starship Troopers (1997)
Summary: The only good bug is a dead bug.. Set in the future, the story follows a young soldier named Johnny Rico and his exploits in the Mobile Infantry. Rico's military career progresses from recruit to non-commissioned officer and finally to officer against the backdrop of an interstellar war between mankind and an arachnoid species known as "the Bugs".
Genres: [Adventure, Action, Thriller, Science Fiction]
Cast: Michael Ironside as Lieutenant Jean Rasczak, Dean Norris as Commanding Officer

ID: tt0123385
Title: Artemisia (1997)
Summary: Her passion for her art changed the face of history.. Artemisia Gentileschi (1593-1653) was one of the first well-known female painters. The movie tells the story of her youth, when she was guided and protected by her father, the painter Orazio Gentileschi. Her professional curiosity about the male anatomy, forbidden for her eyes, led her to the knowledge of sexual pleasure. But she was also well known because in 1612 she had to appear in a courtroom because her teacher, Agostino Tassi, was suspected of raping her. She tried to protect him, but was put in the thumb screws...
Genres: [Drama, History]
Cast: 

ID: tt0109266
Title: Bang (1995)
Summary: Sometimes you've got to break the rules. A young woman in L.A. is having a bad day: she's evicted, an audition ends with a producer furious she won't trade sex for the part, and a policeman nabs her for something she didn't do, demanding fellatio to release her. She snaps, grabs his gun, takes his uniform, and leaves him cuffed to a tree where he's soon having a defenseless chat with a homeless man. She takes off on the cop's motorcycle and, for an afternoon, experiences a cop's life. She talks a young man out of suicide and then is plunged into violence after a friendly encounter with two "vatos." She is torn between self-protection and others' expectations. Is there any resolution for her torrent of feelings?
Genres: [Drama]
Cast: 

ID: tt0119217
Title: Good Will Hunting (1997)
Summary: Some people can never believe in themselves, until someone believes in them.. Will Hunting has a genius-level IQ but chooses to work as a janitor at MIT. When he solves a difficult graduate-level math problem, his talents are discovered by Professor Gerald Lambeau, who decides to help the misguided youth reach his potential. When Will is arrested for attacking a police officer, Professor Lambeau makes a deal to get leniency for him if he will get treatment from therapist Sean Maguire.
Genres: [Drama]
Cast: Robin Williams as Sean Maguire, Matt Damon as Will Hunting, Ben Affleck as Chuckie Sullivan, Stellan Skarsgård as Prof. Gerald Lambeau, Minnie Driver as Skylar

ID: tt0119668
Title: Midnight in the Garden of Good and Evil (1997)
Summary: . A visiting city reporter's assignment suddenly revolves around the murder trial of a local millionaire, whom he befriends.
Genres: [Crime, Drama, Mystery, Thriller]
Cast: John Cusack as John Kelso, Kevin Spacey as James Williams, Bob Gunton as Finley Largent

ID: tt0118230
Title: Full Speed (1996)
Summary: . A brief extract of four kids' lives somewhere in France. Quentin, who won a writers contest and now pays more attention to his career as an author than to his friends, beautiful Julie, his girl-friend, much more mature than she looks, falling in love with Quentin's very best friend Jimmy, who is kind of stuck in his unability of self-expression and grown up under bad social circumstances. And there is the shy boy Samir, exiled from Algeria, who lost his "brother" and only friend some time ago. Samir heavily falls in love with Quentin, but he can't handle it...
Genres: [Romance, Drama]
Cast: 

ID: tt0118715
Title: The Big Lebowski (1998)
Summary: Times like these call for a Big Lebowski.. Jeffrey "The Dude" Lebowski, a Los Angeles slacker who only wants to bowl and drink white Russians, is mistaken for another Jeffrey Lebowski, a wheelchair-bound millionaire, and finds himself dragged into a strange series of events involving nihilists, adult film producers, ferrets, errant toes, and large sums of money.
Genres: [Comedy, Crime]
Cast: Jeff Bridges as The Dude, John Goodman as Walter Sobchak, Julianne Moore as Maude Lebowski, Steve Buscemi as Theodore Donald "Donny" Kerabatsos, Philip Seymour Hoffman as Brandt, John Turturro as Jesus Quintana, David Thewlis as Knox Harrington, Jon Polito as Da Fino

ID: tt0119590
Title: My Life in Pink (1997)
Summary: Sometimes you just have to be yourself.. Ludovic is a small boy who cross-dresses and generally acts like a girl, talks of marrying his neighbor's son and can not understand why everyone is so surprised about it. His actions lead to problems for him and his family.
Genres: [Drama, Comedy]
Cast: 

ID: tt0120594
Title: B. Monkey (1998)
Summary: Trouble never looked so good.. When wistful introvert Alan Furnace meets quick-witted bombshell Beatrice, he has no idea of her secret life as "B. Monkey" -- the top thief-for-hire in London's criminal underworld. Charmed by Furnace's innocent and chivalrous ways, Beatrice resolves to reform. But to cash in on her first chance at real love, she must escape her former partner in crime, the ruthless Paul Neville -- and a dark past that seems to haunt her every step.
Genres: [Romance, Crime, Drama]
Cast: Jared Harris as Alan Furnace, Rupert Everett as Paul Neville, Ian Hart as Steve Davis

ID: tt0120888
Title: The Wedding Singer (1998)
Summary: He's gonna party like it's 1985.. Robbie, a local rock star turned wedding singer, is dumped on the day of his wedding. Meanwhile, waitress Julia finally sets a wedding date with her fiancée Glenn. When Julia and Robbie meet and hit it off, they find that things are more complicated than anybody thought.
Genres: [Romance, Comedy]
Cast: Adam Sandler as Robbie Hart, Drew Barrymore as Julia Sullivan, Jon Lovitz as Jimmie Moore, Steve Buscemi as David 'Dave' Veltri

ID: tt0120782
Title: Palmetto (1998)
Summary: In a town this bad, it's hard to be good.... A recently released ex-con gets involved in a fake kidnapping scheme that turns very real.
Genres: [Mystery, Thriller, Drama, Romance, Crime]
Cast: Woody Harrelson as Harry Barber, Gina Gershon as Nina, Elisabeth Shue as Mrs. Donnelly / Rhea Malroux, Michael Rapaport as Donnely

ID: tt0119822
Title: As Good as It Gets (1997)
Summary: A comedy from the heart that goes for the throat.. New York City. Melvin Udall, a cranky, bigoted, obsessive-compulsive writer, finds his life turned upside down when neighboring gay artist Simon is hospitalized and his dog is entrusted to Melvin. In addition, Carol, the only waitress who will tolerate him, must leave work to care for her sick son, making it impossible for Melvin to eat breakfast.
Genres: [Comedy, Romance]
Cast: Jack Nicholson as Melvin Udall, Cuba Gooding Jr. as Frank Sachs, Skeet Ulrich as Vincent Lopiano, Harold Ramis as Dr. Bettes, Brian Doyle-Murray as Handyman, Matt Malloy as Men's Store Salesman

ID: tt0119574
Title: Love and Death on Long Island (1997)
Summary: . Giles De'Ath is a widower who doesn't like anything modern. He goes to movies and falls in love with film star, Ronnie Bostock. He then investigates everything about the movie and Ronnie. After that he travels to Long Island city where Ronnie lives and meets him, pretending that Ronnie is a great actor and that's why Giles admires him.
Genres: [Drama, Romance, Foreign]
Cast: John Hurt as Giles De'Ath, Maury Chaykin as Irving 'Irv' Buckmuller

ID: tt0120890
Title: Wild Things (1998)
Summary: They're dying to play with you.. When teen-socialite Kelly Van Ryan (Richards) and troubled bad girl Suzie Toller (Campbell) accuse guidance counselor Sam Lombardo (Dillon) of rape, he's suspended by the school, rejected by the town, and fighting to get his life back. One cop (Bacon) suspects conspiracy, but nothing is what it seems...
Genres: [Thriller]
Cast: Matt Dillon as Sam Lombardo, Kevin Bacon as Sergeant Ray Duquette, Bill Murray as Kenneth Bowden

ID: tt0119780
Title: Niagara, Niagara (1997)
Summary: Love can't be controlled.. "Niagra, Niagra" begins quietly in a drugstore in Poughkeepsie, where Marcie, the film's disarming heroine, likes to shoplift. She literally crashes into Seth, a quiet outsider, also on a shoplifting spree. Marcie invites Seth to accompany her to Canada to find a black hairstyling head. They set off in Seth's beat-up station wagon, destined for a toy store in Toronto. While on the road, Marcie confides to Seth that she has Tourette's syndrome, necessitating a series of detours to liquor stories and pharmacies along the roads of upstate New York.
Genres: [Drama, Romance]
Cast: 

ID: tt0120510
Title: Wide Awake (1998)
Summary: Meeting your best friend. Finding your favorite teacher. Having your first crush. Remember what it felt like to be.... A feel-good comedy about the laughter, excitement and fun that come with being a kid! Joshua is a thoughtful 10-year-old looking for some simple answers to life's eternal questions. The problem is that no one - including his concerned parents and his 5th grade teacher - seems to be making any sense!
Genres: [Comedy, Drama, Family]
Cast: Denis Leary as Mr. Beal, Rosie O'Donnell as Sister Terry

ID: tt0120765
Title: My Giant (1998)
Summary: A comedy of incredible proportions.. Billy Crystal plays a Hollywood agent who stumbles upon Max, a giant living in Romania, and tries to get him into the movies.
Genres: [Comedy, Drama, Family]
Cast: Billy Crystal as Sam 'Sammy' Kamin, Kathleen Quinlan as Serena Kamin, Joss Ackland as Monsignor Popesou

ID: tt0120213
Title: Slappy and the Stinkers (1998)
Summary: Five Kids. One Sea-Lion. No Rules.. Five 2nd-grade kids who don't follow strict rules by their school principal Brinway are dubbed "Stinkers" by him. On the class visit to an aquarium the Stinkers decide that a sea lion called Slappy doesn't feel too good there, "free" him, and plant him into Brinway's hot-tub.
Genres: [Adventure, Family]
Cast: 

ID: tt0120906
Title: Zero Effect (1998)
Summary: The world's most private detective.. Daryl Zero is a private investigator. Along with his assistant, Steve Arlo he solves impossible crimes and puzzles. Though a master investigator, when he is not working, Zero doesn't know what to do with himself. He has no social skills, writes bad music, and drives Arlo crazy. In his latest case, Zero must find out who is blackmailing a rich executive, and when his client won't tell him, why.
Genres: [Comedy, Crime, Mystery, Thriller]
Cast: Bill Pullman as Daryl Zero, Ben Stiller as Steve Arlo

ID: tt0118577
Title: An Alan Smithee Film: Burn, Hollywood, Burn (1998)
Summary: . Filmmaker Alan Smithee finds himself the unwilling puppet of a potentially bad, big budget action film which he proceeds to steal the reels and leave the cast and crew in a frenzy.
Genres: [Comedy]
Cast: Jackie Chan as Jackie Chan, Sylvester Stallone as Sylvester Stallone, Whoopi Goldberg as Whoopi Goldberg, Eric Idle as Alan Smithee, Billy Bob Thornton as Billy Bob Thornton

ID: tt0113184
Title: Go Now (1995)
Summary: . Nick, is a young Scottish soccer player living in the big city. He meets Karen, and the two fall in love and move in together. Soon after, Nick exhibits signs of serious illness. As his body slowly succumbs to multiple sclerosis, he experiences a wide sweep of jagged emotions, and in the process gives himself and those who love him the strength to carry on.
Genres: [Romance, Drama, TV Movie]
Cast: 

ID: tt0120800
Title: Quest for Camelot (1998)
Summary: An evil knight gives nobility a bad name.. During the times of King Arthur, Kayley is a brave girl who dreams of following her late father as a Knight of the Round Table. The evil Ruber wants to invade Camelot and take the throne of King Arthur, and Kayley has to stop him.
Genres: [Fantasy, Animation, Drama, Romance, Family]
Cast: Cary Elwes as Garrett (voice), Gary Oldman as Ruber (voice), Pierce Brosnan as King Arthur (voice), Gabriel Byrne as Lionel (voice), John Gielgud as Merlin (voice), Frank Welker as Ayden (voice), Eric Idle as Devon (voice)

ID: tt0119547
Title: Little Boy Blue (1997)
Summary: . Living in rural Texas is a dysfunctional family: an abusive dad, a Vietnam vet with a war wound that's left him impotent; a compliant wife and a son of about 20, two small sons who look a lot like their brother. The dad harbors a secret, and he goes to murderous lengths to keep it hidden. The young man, Jimmy, who has suspicions, but little comes out until a Yankee woman comes to town.
Genres: [Drama, Thriller]
Cast: Ryan Phillippe as Jimmy West, Nastassja Kinski as Kate West, John Savage as Ray West

ID: tt0120401
Title: The Ugly (1997)
Summary: Murder is in the eye of the beholder.. A female psychologist wants to understand the minds of a confessed serial killer who spent the last five years in a mental hospital because of his state.
Genres: [Horror, Thriller]
Cast: 

ID: tt0120828
Title: Six Days Seven Nights (1998)
Summary: After this week in paradise, they’re going to need a vacation.. When Quinn, a grouchy pilot living the good life in the South Pacific, agrees to transfer a savvy fashion editor, Robin, to Tahiti, he ends up stranded on a deserted island with her after their plane crashes. The pair avoid each other at first, until they're forced to team up to escape from the island -- and some pirates who want their heads.
Genres: [Action, Adventure, Comedy, Romance]
Cast: Harrison Ford as Quinn Harris, Anne Heche as Robin Monroe, Allison Janney as Marjorie, Danny Trejo as Pierce

ID: tt0118998
Title: Doctor Dolittle (1998)
Summary: He doesn't just talk to the animals!. A successful physician and devoted family man, John Dolittle (Eddie Murphy) seems to have the world by the tail, until a long suppressed talent he possessed as a child, the ability to communicate with animals is suddenly reawakened with a vengeance! Now every creature within squawking distance wants the good doctor's advice, unleashing an outrageous chain of events that turns his world upside down!
Genres: [Comedy, Family, Fantasy]
Cast: Eddie Murphy as Dr. John Dolittle, Oliver Platt as Dr. Mark Weller, Peter Boyle as Calloway, Richard Schiff as Dr. Gene Reiss, Jeffrey Tambor as Dr. Fish, Beth Grant as Woman, Albert Brooks as Jacob "Jake" the Tiger, Paul Giamatti as Blaine Hammersmith

ID: tt0120591
Title: Armageddon (1998)
Summary: The Earth's Darkest Day Will Be Man's Finest Hour. When an asteroid threatens to collide with Earth, NASA honcho Dan Truman determines the only way to stop it is to drill into its surface and detonate a nuclear bomb. This leads him to renowned driller Harry Stamper, who agrees to helm the dangerous space mission provided he can bring along his own hotshot crew. Among them is the cocksure A.J. who Harry thinks isn't good enough for his daughter, until the mission proves otherwise.
Genres: [Action, Thriller, Science Fiction, Adventure]
Cast: Bruce Willis as Harry S. Stamper, Billy Bob Thornton as Dan Truman, NASA Administrator, Ben Affleck as A.J. Frost, Will Patton as Charles 'Chick' Chapple, Steve Buscemi as Rockhound, Chris Ellis as Flight Director Clark, Keith David as General Kimsey, Charlton Heston as Narrator, Udo Kier as Psychologist, Matt Malloy as NASA Techs

ID: tt0036872
Title: Going My Way (1944)
Summary: When the St. Louis Browns lost Bing, the Cardinal got a good singer!. Youthful Father Chuck O'Malley (Bing Crosby) led a colourful life of sports, song, and romance before joining the Roman Catholic clergy. After being appointed to a run-down New York parish , O'Malley's worldly knowledge helps him connect with a gang of boys looking for direction, eventually winning over the aging, conventional Parish priest (Barry Fitzgerald).
Genres: [Drama, Comedy, Music]
Cast: 

ID: tt0041113
Title: All the King's Men (1949)
Summary: He Might Have Been A Pretty Good Guy … If Too Much Power … And Women … Hadn’t Gone To his Head!. All The King's Men is the story of the rise of politician Willie Stark from a rural county seat to the governor's mansion.
Genres: [Drama]
Cast: Sam Harris as Politician

ID: tt0086425
Title: Terms of Endearment (1983)
Summary: . Aurora and Emma are mother and daughter who march to different drummers. Beginning with Emma's marriage, Aurora shows how difficult and loving she can be. The movie covers several years of their lives as each finds different reasons to go on living and find joy. Aurora's interludes with Garrett Breedlove, retired astronaut and next door neighbor are quite striking.
Genres: [Drama, Comedy]
Cast: Shirley MacLaine as Aurora Greenway, Jack Nicholson as Garrett Breedlove, Danny DeVito as Vernon Dahlart, Jeff Daniels as Flap Horton, John Lithgow as Sam Burns

ID: tt0093629
Title: A Nightmare on Elm Street 3: Dream Warriors (1987)
Summary: If you think you're ready for Freddy, think again!. It's been many years since Freddy Krueger's first victim, Nancy, came face-to-face with Freddy and his sadistic, evil ways. Now, Nancy's all grown up; she's put her frightening nightmares behind her and is helping teens cope with their dreams. Too bad Freddy's decided to herald his return by invading the kids' dreams and scaring them into committing suicide.
Genres: [Horror, Thriller]
Cast: Robert Englund as Freddy Krueger, Patricia Arquette as Kristen Parker, Laurence Fishburne as Max

ID: tt0089173
Title: Friday the 13th: A New Beginning (1985)
Summary: A New Beginning to the first step in terror.. Homicidal maniac Jason returns from the grave to cause more bloody mayhem. Young Tommy may have escaped from Crystal Lake, but he’s still haunted by the gruesome events that happened there. When gory murders start happening at the secluded halfway house for troubled teens where he now lives, it seems like his nightmarish nemesis, Jason, is back for more sadistic slaughters. But as things spiral out of control and the body count rises, Tommy begins to wonder if he’s become the killer he fears most.
Genres: [Horror, Thriller]
Cast: Corey Feldman as Tommy Jarvis Age 12

ID: tt0095271
Title: Halloween 4: The Return of Michael Myers (1988)
Summary: Horror has returned to Haddonfield.. The legend of that creepy masked-man, Michael Myers, comes to life once again in this fourth installment of the successful horror franchise. This time, it's Michael's niece, Jamie, who can't seem to escape her crazy uncle. With Michael on the loose, Jamie enlists the help of good old Dr. Loomis to stop the murderer. This time, though, there seems to be no end to Michael's madness.
Genres: [Horror, Thriller]
Cast: Donald Pleasence as Dr. Sam Loomis

ID: tt0081383
Title: Prom Night (1980)
Summary: If you're not back by midnight... you won't be coming home!. This slasher movie follows a relentless killer who is out to avenge the death of a young girl who died after being bullied and teased by four of her classmates. Now high-school students, the guilt-ridden kids have kept their involvement a secret, but when they start being murdered, one by one, it's clear that someone knows the truth. Also coping with the past are members of the dead girl's family, most notably her prom-queen sister, Kim Hammond (Jamie Lee Curtis).
Genres: [Horror, Mystery, Thriller]
Cast: Leslie Nielsen as Mr. Hammond, Jamie Lee Curtis as Kim

ID: tt0093176
Title: Hello Mary Lou: Prom Night II (1987)
Summary: You Can't Keep a Bad Girl Down. When Hamilton High's Prom Queen of 1957, Mary Lou Maloney is killed by her jilted boyfriend, she comes back for revenge 30 years later. Bill Nordham is now the principle of Hamilton High and his son is about to attend the prom with Vicki Carpenter. However, she is possessed by Mary Lou Maloney after opening a trunk in the school's basement. Now Bill must face the horror he left behind in 1957.
Genres: [Horror]
Cast: Michael Ironside as Bill Nordham

ID: tt0103956
Title: Child's Play 3 (1991)
Summary: Chucky has a new playmate.. It's been eight years since the events in the second film, we now see that Andy is a teenager who has been enrolled in a military school. Play Pals Toy Company decides to re-release its Good Guys line, feeling that after all this time, the bad publicity has died down. As they re-used old materials, the spirit of Charles Lee Ray once again comes to life.
Genres: [Thriller, Horror, Action]
Cast: Brad Dourif as Chucky

ID: tt0093409
Title: Lethal Weapon (1987)
Summary: If these two can learn to stand each other... the bad guys don't stand a chance.. Veteran buttoned-down LAPD detective Roger Murtaugh is partnered with unhinged cop Martin Riggs, who -- distraught after his wife's death -- has a death wish and takes unnecessary risks with criminals at every turn. The odd couple embark on their first homicide investigation as partners, involving a young woman known to Murtaugh with ties to a drug and prostitution ring.
Genres: [Adventure, Action, Comedy, Thriller, Crime]
Cast: Mel Gibson as Sergeant Martin Riggs, Danny Glover as Sergeant Roger Murtaugh, Gary Busey as Mr. Joshua, Mitchell Ryan as General Peter McAllister, Mary Ellen Trainor as Psychologist, Steve Kahan as Captain Ed Murphy

ID: tt0117898
Title: This World, Then the Fireworks (1997)
Summary: Meet Marty and Carol. Two people who are very good at being bad.. Marty Lakewood is a reporter forced to leave Chicago and his family because he had uncovered too much police corruption. He returns to his small home town on the California coast to his ailing mother and prostitute sister, with whom he had an incestuous affair. Being short of money, he seduces a woman cop in order to sell her house.
Genres: [Crime, Drama, Thriller]
Cast: Billy Zane as Marty Lakewood, Richard Edson as Joe, Gina Gershon as Carol Lakewood Morton, Seymour Cassel as Detective Harris, Will Patton as Lt. Morgan

ID: tt0102757
Title: The Rapture (1991)
Summary: "It goes places movies seldom dare to go.". A lonely telephone operator leading an empty, amoral life finds God — only to have her faith continually tested in ways beyond what she could ever have imagined.
Genres: [Mystery, Drama]
Cast: Mimi Rogers as Sharon, Will Patton as Deputy Foster, James Le Gros as Tommy

ID: tt0134619
Title: Disturbing Behavior (1998)
Summary: In Cradle Bay it doesn’t matter if you’re not perfect.  You will be.. Steve Clark (James Marsden) is a newcomer in the town of Cradle Bay, and he quickly realizes that there's something odd about his high school classmates. The clique known as the "Blue Ribbons" are the eerie embodiment of academic excellence and clean living. But, like the rest of the town, they're a little too perfect. When Steve's rebellious friend Gavin (Nick Stahl) mysteriously joins their ranks, Steve searches for the truth with fellow misfit Rachel (Katie Holmes).
Genres: [Mystery, Horror, Science Fiction]
Cast: 

ID: tt0137386
Title: Billy's Hollywood Screen Kiss (1998)
Summary: Lights!... Camera!... Attraction!. Billy, a struggling young gay photographer (who likes Polaroids), tired of being the "other man", falls in love with Gabriel, a waiter and aspiring musician who is probably straight but possibly gay or at least curious. Billy tries to get Gabriel to model for his latest project, a series of remakes of famous Hollywood screen kisses, featuring male couples, while also trying to win his affections.
Genres: [Comedy, Romance]
Cast: 

ID: tt0062737
Title: Blackbeard's Ghost (1968)
Summary: He's out'a sight...literally!. Peter Ustinov stars as the eponymous wraith, who returns to Earth to aid his descendant, elderly Elsa Lanchester (Stowecroft). The villains want to kick Lanchester and her friends out of their group home so that they can build a crooked casino. Good guy Steve Walker (Jones) gets caught in the middle of the squabble after evoking Blackbeard's ghost.
Genres: [Fantasy, Comedy, Family]
Cast: 

ID: tt0075807
Title: Candleshoe (1977)
Summary: . Candleshoe is a 1977 Walt Disney Productions live action movie based on the Michael Innes novel Christmas at Candleshoe and starring Jodie Foster, Helen Hayes in her last screen appearance, David Niven and Leo McKern.
Genres: [Family]
Cast: Jodie Foster as Casey

ID: tt0065566
Title: The Computer Wore Tennis Shoes (1969)
Summary: . Some college students manage to persuade the town's big businessman, A. J. Arno, to donate a computer to their college. When the problem- student, Dexter Riley, tries to fix the computer, he gets an electric shock and his brain turns to a computer; now he remembers everything he reads. Unfortunately, he also remembers information which was in the computer's memory, like Arno's illegal businesses..
Genres: [Comedy, Family]
Cast: Kurt Russell as Dexter Riley, Frank Welker as Henry

ID: tt0089853
Title: The Purple Rose of Cairo (1985)
Summary: . Cecilia is a waitress in New Jersey, living a dreary life during the Great Depression. Her only escape from her mudane reality is the movie theatre. After losing her job, Cecilia goes to see 'The Purple Rose of Cairo' in hopes of raising her spirits, where she watches dashing archaeologist Tom Baxter time and again.
Genres: [Fantasy, Comedy, Romance]
Cast: Mia Farrow as Cecilia, Jeff Daniels as Tom Baxter / Gil Shepherd, Danny Aiello as Monk, John Wood as Jason, Dianne Wiest as Emma, Glenne Headly as Hooker

ID: tt0046183
Title: Peter Pan (1953)
Summary: It will live in your heart forever!. Leaving the safety of their nursery behind, Wendy, Michael and John follow Peter Pan to a magical world where childhood lasts forever. But while in Neverland, the kids must face Captain Hook and foil his attempts to get rid of Peter for good.
Genres: [Animation, Music, Family, Adventure, Fantasy]
Cast: 

ID: tt0120694
Title: Halloween: H20 (1998)
Summary: Blood is thicker than Water.. Two decades after surviving a massacre on October 31, 1978, former baby sitter Laurie Strode finds herself hunted by persistent knife-wielder Michael Myers. Laurie now lives in Northern California under an assumed name, where she works as the headmistress of a private school. But it's not far enough to escape Myers, who soon discovers her whereabouts. As Halloween descends upon Laurie's peaceful community, a feeling of dread weighs upon her -- with good reason.
Genres: [Horror, Thriller]
Cast: Jamie Lee Curtis as Laurie Strode / Keri Tate

ID: tt0079367
Title: The Jerk (1979)
Summary: A rags to riches to rags story.. After discovering he's not really black like the rest of his family, likable dimwit Navin Johnson runs off on a hilarious misadventure in this comedy classic that takes him from rags to riches and back to rags again. The slaphappy jerk strikes it rich, but life in the fast lane isn't all it's cracked up to be and, in the end, all that really matters to Johnson is his true love.
Genres: [Comedy]
Cast: Steve Martin as Navin R. Johnson, M. Emmet Walsh as Madman

ID: tt0090305
Title: Weird Science (1985)
Summary: If you can't get a date, make one!. Two unpopular teenagers, Gary and Wyatt, fail at all attempts to be accepted by their peers. Their desperation to be liked leads them to "create" a woman via their computer. Their living and breathing creation is a gorgeous woman, Lisa, whose purpose is to boost their confidence level by putting them into situations which require Gary and Wyatt to act like men.
Genres: [Comedy, Romance, Fantasy]
Cast: Bill Paxton as Chet Donnelly, Robert Downey Jr. as Ian

ID: tt0061584
Title: Doctor Dolittle (1967)
Summary: Ride across the sea inside the GIANT PINK SEA SNAIL!. Get ready for the wildest adventure of a lifetime in the most ambitious production ever brought to film. Earning a 1967 Academy Award nomination for Best Picture, this dazzling fantasy turns both ordinary and exotic animals into talking, dancing and singing sensations! Rex Harrison is unforgettable in this inspiring adaptation of Hugh Lofting's classic stories.Step into the English country home of the good doctor as he performs remarkable treatments on the wildest variety of patients you could imagine. Discover his secret cures and watch with wide-eyed excitement as he and his four-legged, fine-feathered friends charm their way into your heart!
Genres: [Adventure, Fantasy, Comedy, Family, Music, Romance]
Cast: 

ID: tt0057372
Title: The Nutty Professor (1963)
Summary: What does he become? What kind of monster?. Jerry Lewis directed, co-wrote and starred in this riotously funny movie that set a new standard for screen comedy and inspired the hit remake. Lewis plays a timid, nearsighted chemistry teacher who discovers a magical potion that can transform him into a suave and handsome Romeo. The Jekyll and Hyde game works well enough until the concoction starts to wear off at the most embarrassing times.
Genres: [Comedy, Family, Romance, Science Fiction]
Cast: Kathleen Freeman as Millie Lemmon

ID: tt0070016
Title: Charlotte's Web (1973)
Summary: That humble radiant terrific book is now a humble radiant terrific movie.. Wilbur the pig is scared of the end of the season, because he knows that come that time, he will end up on the dinner table. He hatches a plan with Charlotte, a spider that lives in his pen, to ensure that this will never happen.
Genres: [Animation, Drama, Family]
Cast: 

ID: tt0091790
Title: Pretty in Pink (1986)
Summary: Blane's a pretty cool guy. Andie's pretty in pink. And Ducky's pretty crazy.. Andie is an outcast, hanging out either with her older boss, who owns the record store where she works, or her quirky high school classmate Duckie, who has a crush on her. When one of the rich and popular kids at school, Blane, asks Andie out, it seems too good to be true. As Andie starts falling for Blane, she begins to realize that dating someone from a different social sphere is not easy.
Genres: [Comedy]
Cast: Andrew McCarthy as Blane McDonough, Harry Dean Stanton as Jack Walsh, James Spader as Steff, Gina Gershon as Trombley, Girl Friend/Gym Class

ID: tt0120703
Title: How Stella Got Her Groove Back (1998)
Summary: Sometimes you have to break the rules to free your heart.. Through good times and bad, Stella and Delilah have always had each other. Now, Stella's so busy building a life that she's forgotten how to really live. But Delilah is about to change all that. What starts as a quick trip to Jamaica, end as an exhilarating voyage of self discovery as Stella learns to open her heart and find love - even if it's with a man 20 years her junior.
Genres: [Comedy, Drama, Romance]
Cast: Angela Bassett as Stella Payne, Whoopi Goldberg as Delilah Abraham, Danny Glover as (scenes deleted)

ID: tt0099763
Title: Henry: Portrait of a Serial Killer (1986)
Summary: The shocking true story of Henry Lee Lucas.. Henry likes to kill people, in different ways each time. Henry shares an apartment with Otis. When Otis' sister comes to stay, we see both sides of Henry: "the guy next door" and the serial killer.
Genres: [Crime, Drama, Horror, Thriller]
Cast: Michael Rooker as Henry

ID: tt0120611
Title: Blade (1998)
Summary: Part Man. Part Vampire. All Hero.. When Blade's mother was bitten by a vampire during pregnancy, she did not know that she gave her son a special gift while dying: All the good vampire attributes in combination with the best human skills. Blade and his mentor Whistler battle an evil vampire rebel (Deacon Frost) who plans to take over the outdated vampire council, capture Blade and resurrect voracious blood god La Magra.
Genres: [Horror, Action]
Cast: Wesley Snipes as Blade, Kris Kristofferson as Whistler, Donal Logue as Quinn, Udo Kier as Dragonetti

ID: tt0120576
Title: Dance with Me (1998)
Summary: In the dance of life, love can happen in a heartbeat.. Young Cuban Rafael just buried his mother, and comes to Houston to meet his father John for the first time. The difficult part is that John doesn't know he is Rafael's father. John runs a dance studio, and everyone prepares for the World Open Dance championship in Las Vegas. It soon becomes clear Rafael is a very good dancer, and Ruby is the biggest hope for the studio at the championship.
Genres: [Drama, Music, Romance]
Cast: Kris Kristofferson as John Burnett, Joan Plowright as Bea Johnson, Beth Grant as Lovejoy

ID: tt0120901
Title: Wrongfully Accused (1998)
Summary: It's not just a movie. It's every movie.. Ryan Harrison, a violin god, superstar and sex symbol does not want to cheat on sexy Lauren Goodhue's husband with her. Mr. Goodhue is found murdered and Ryan suddenly finds himself being the main suspect. After being sentenced to death he manages to flee while being transferred to his execution site. Now, all the world is after him as he stumbles from one unfortunate incident to the next in order to prove himself innocent - by finding a mysterious one-eyed, one-armed, one-legged man...
Genres: [Action, Adventure, Comedy]
Cast: Leslie Nielsen as Ryan Harrison

ID: tt0119346
Title: I Married a Strange Person! (1998)
Summary: . A newlywed develops a strange lump on his neck that gives him the ability to transform people or objects at will. His wife is very upset. Meanwhile, the CEO of Smilecorp learns of this man and his ability and sees a way to achieve world domination if only the man can be taken alive. Animated movie by Bill Plympton.
Genres: [Drama, Comedy, Animation]
Cast: 

ID: tt0120654
Title: Dirty Work (1998)
Summary: Revenge is a dish best served DIRTY.. Unemployed and recently dumped, Mitch and his buddy Sam start a revenge-for-hire business to raise the $50,000 that Sam's father needs to get a heart transplant. The dirty duo brings down a movie theater manager and hires hookers to pose as dead bodies during a live TV ad. When a wealthy developer hires the guys to trash a building (so that he can have it condemned), problems arise and a feud ensues.
Genres: [Comedy]
Cast: Jack Warden as 'Pops' McKenna, Christopher McDonald as Travis Cole, Chevy Chase as Dr. Farthing, John Goodman as Mayor Adrian Riggins, Adam Sandler as Satan

ID: tt0035279
Title: Saboteur (1942)
Summary: You'd like to say - IT CAN'T HAPPEN HERE!... but every jolting scene is TRUE!!. Aircraft factory worker Barry Kane goes on the run across the United States when he is wrongly accused of starting a fire that killed his best friend.
Genres: [Thriller, War]
Cast: 

ID: tt0021015
Title: Juno and the Paycock (1930)
Summary: . During the Irish revolution, a family earns a big inheritance. They start leading a rich life forgetting what the most important values of are. At the end, they discover they will not receive that inheritance; the family is destroyed and penniless. They must sell their home and start living like vagabonds.
Genres: [Drama]
Cast: 

ID: tt0085346
Title: Class (1983)
Summary: The good news is, Jonathan's having his first affair. The bad news is, she's his roommate's mother!. Naive Midwestern prep student Jonathan bonds with his more worldly roommate, Skip, who takes the small-town boy under his wing. At Skip's urging, the inexperienced Jonathan is emboldened to seek out older women in the cocktail lounges of nearby Chicago, where he meets and beds the alluring Ellen, who unfortunately turns out to be Skip's mother. The division between the friends is further deepened when a cheating scandal engulfs the school.
Genres: [Comedy, Drama]
Cast: Andrew McCarthy as Jonathan Ogner, John Cusack as Roscoe Maibaum

ID: tt0087359
Title: Grandview, U.S.A. (1984)
Summary: Where dreams have a funny way of coming true.. Life in the small town of Grandview, Illinois is one that is just like any other city or town. Tim Pearson, soon to be graduating high school wants to go to Florida to study oceanography. He meets Michelle "Mike" Cody and is attracted to her. She runs the local Demolition Derby place. Ernie "Slam" Webster is on of the drivers in the derby who's wife is cheating on him and wants to later on be with Mike. Tim falls for Mike and a big love triangle is about to happen.
Genres: [Drama]
Cast: Jamie Lee Curtis as Michelle 'Mike' Cody, Patrick Swayze as Ernie 'Slam' Webster, Jennifer Jason Leigh as Candy Webster, M. Emmet Walsh as Mr. Clark, John Cusack as Johnny Maine, Joan Cusack as Mary Maine

ID: tt0096463
Title: Working Girl (1988)
Summary: For anyone who's ever won. For anyone who's ever lost. And for everyone who's still in there trying.. Witty, romantic look at life in the corporate jungle. Tess McGill is an ambitious secretary with a unique approach for climbing the ladder to success. When her classy, but villainous boss breaks a leg skiing, Tess takes over her office, her apartment and even her wardrobe. She creates a deal with a handsome investment banker that will either take her to the top, or finish her off for good.
Genres: [Comedy, Drama, Romance]
Cast: Harrison Ford as Jack Trainer, Melanie Griffith as Tess McGill, Sigourney Weaver as Katharine Parker, Alec Baldwin as Mick Dugan, Joan Cusack as Cyn, Philip Bosco as Oren Trask, Oliver Platt as Lutz, Kevin Spacey as Bob Speck, Timothy Carhart as Tim Draper, Zach Grenier as Jim, Caroline Aaron as Petty Marsh Secretary

ID: tt0095593
Title: Married to the Mob (1988)
Summary: They're her family... Whether she likes it or not.. Angela de Marco is fed up with her gangster husband's line of work and wants no part of the crime world. When her husband is killed for having an affair with the mistress of mob boss Tony "The Tiger" Russo, Angela and her son depart for New York City to make a fresh start. Unfortunately, Tony has set his sights upon Angela -- and so has an undercover FBI agent looking to use her to bust Tony.
Genres: [Comedy, Crime, Romance]
Cast: Michelle Pfeiffer as Angela de Marco, Matthew Modine as Mike Downey, Oliver Platt as Ed Benitez, Alec Baldwin as Frank de Marco, Dean Stockwell as Tony Russo, Joan Cusack as Rose, Joe Spinell as Leonard 'Tiptoes' Mazzilli, Charles Napier as Angela's Hairdresser, Tracey Walter as Mr. Chicken Lickin'

ID: tt0084938
Title: Young Doctors in Love (1982)
Summary: Warning: This Movie May Be Dangerous To Your Health. You May Never Stop Laughing.. An "Airplane!"-style spoof of hospital soap operas: a brilliant young trainee can't stand the sight of blood; a doctor romances the head nurse in order to get the key to the drugs cabinet; there's a mafioso on the loose disguised as a woman - in other words all the usual ingredients present and correct, though in this case the laughs are intentional.
Genres: [Comedy]
Cast: Michael McKean as Dr. Simon August, Sean Young as Dr. Stephanie Brody, Héctor Elizondo as Angelo / Angela Bonafetti, Harry Dean Stanton as Dr. Oliver Ludwig, Taylor Negron as Dr. Phil Burns, Demi Moore as New intern, Ed Begley Jr. as The Flashback - Young Simon's Father

ID: tt0084472
Title: Parasite (1982)
Summary: You will not feel the terror until you experience the movie!. Paul Dean has created a deadly parasite that is now attached to his stomach. He and his female companion, Patricia Welles, must find a way to destroy it while also trying to avoid Ricus, his rednecks, and an evil government agent named Merchant.
Genres: [Horror, Science Fiction]
Cast: Demi Moore as Patricia Welles

ID: tt0104257
Title: A Few Good Men (1992)
Summary: . When cocky military lawyer Lt. Daniel Kaffee and his co-counsel, Lt. Cmdr. JoAnne Galloway, are assigned to a murder case, they uncover a hazing ritual that could implicate high-ranking officials such as shady Col. Nathan Jessep.
Genres: [Drama]
Cast: Tom Cruise as Lt. Daniel Kaffee, Jack Nicholson as Col. Nathan R. Jessep, Demi Moore as Lt. Cdr. JoAnne Galloway, Kevin Bacon as Capt. Jack Ross, Kevin Pollak as Lt. Sam Weinberg, J.T. Walsh as Lt. Col. Matthew A. Markinson, Kiefer Sutherland as Lt. Jonathan Kendrick, Xander Berkeley as Capt. Whitaker, Cuba Gooding Jr. as Cpl. Carl Hammaker

ID: tt0120162
Title: Somewhere in the City (1998)
Summary: . The film portrays life of New York City's Lower East Side inhabitants. Introvert therapist Betty is befriended with Chinese immigrant Lu Lu, leftist radical Che is also Lu Lu's friend. Italian immigrant and wife of supervisor of building where they all live, Marta, is having an affair with her neighbour Frankie who likes scams. One of his scams goes terribly wrong.
Genres: [Comedy, Drama]
Cast: Robert John Burke as Frankie

ID: tt0146336
Title: Urban Legend (1998)
Summary: It Happened To Someone Who Knows Someone You Know... You're Next.. There's a campus killer on the loose who's making urban legends, like the one about eating pop rocks and soda at the same time will make your stomach explode and the one about a psycho with an axe stepping into the backseat of your car at the gas station when not looking, into reality.
Genres: [Horror, Thriller]
Cast: Robert Englund as Professor William Wexler

ID: tt0126604
Title: Pecker (1998)
Summary: He never realized how far 35 millimeters would take him.. A Baltimore sandwich shop employee becomes an overnight sensation when photographs he's taken of his weird family become the latest rage in the art world. The young man is called "Pecker" because he pecks at his food like a bird.
Genres: [Comedy]
Cast: Christina Ricci as Shelley, Martha Plimpton as Tina, Lili Taylor as Rorey Wheeler

ID: tt0080731
Title: The Fiendish Plot of Dr. Fu Manchu (1980)
Summary: BEWARE! See this movie - An hour later you'll want to see it again!. Fu Manchu's 168th birthday celebration is dampened when a hapless flunky spills Fu's age-regressing elixir vitae. Fu sends his lackeys to round up ingredients for a new batch of elixir, starting with the Star of Leningrad diamond, nabbed from a Soviet exhibition in Washington. The FBI sends agents Capone and Williams to England to confer with Nayland Smith, an expert on Fu.
Genres: [Comedy, Horror]
Cast: Helen Mirren as Alice Rage

ID: tt0120889
Title: What Dreams May Come (1998)
Summary: After life there is more. The end is just the beginning.. Chris Neilson dies to find himself in a heaven more amazing than he could have ever dreamed of. There is one thing missing: his wife. After he dies, his wife, Annie killed herself and went to hell. Chris decides to risk eternity in hades for the small chance that he will be able to bring her back to heaven.
Genres: [Drama, Fantasy, Romance]
Cast: Robin Williams as Chris Nielsen, Cuba Gooding Jr. as Albert Lewis, Annabella Sciorra as Annie Collins-Nielsen, Max von Sydow as The Tracker, Lucinda Jenney as Mrs. Jacobs

ID: tt0073440
Title: Nashville (1975)
Summary: Wild. Wonderful. Sinful. Laughing. Explosive.. This movie tells the intersecting stories of various people connected to the music business in Nashville. Barbara Jean is the reigning queen of Nashville but is near collapse. Linnea and Delbert Reese have a shaky marriage and 2 deaf children. Opal is a British journalist touring the area. These and other stories come together in a dramatic climax.
Genres: [Drama, Music]
Cast: Ned Beatty as Delbert Reese, Shelley Duvall as Marthe aka "L. A. Joan", Scott Glenn as Pfc. Glenn Kelly, Jeff Goldblum as Tricycle Man, Lily Tomlin as Linnea Reese, Keenan Wynn as Mr. Green, Elliott Gould as Himself

ID: tt0119577
Title: Love Is the Devil: Study for a Portrait of Francis Bacon (1998)
Summary: . Biography of the British painter Francis Bacon. The movie focuses on his relationship with George Dyer, his lover. Dyer was a former small time crook.
Genres: [TV Movie, Drama]
Cast: 

ID: tt0090830
Title: Children of a Lesser God (1986)
Summary: Love has a language all of its own.. James is a new speech teacher at a school for the deaf. He falls for Sarah, a pupil who decided to stay on at the school rather than venture into the big bad world. She shuns him at first, refusing to read his lips and only using signs. Will her feelings change over time?
Genres: [Drama, Romance]
Cast: William Hurt as James Leeds, Philip Bosco as Dr. Curtis Franklin

ID: tt0124819
Title: Orgazmo (1997)
Summary: Fighting crime never felt so good!. Joe Young is a devout Mormon living in L.A. trying to raise enough money to go back to Utah and marry his girlfriend, Lisa. Joe is spreading the word about the church of Latter Day Saints one day when he's confronted by two burly bodyguards. A scuffle breaks out, and Joe's martial arts skills impress Maxxx Orbison, who directs pornographic movies.
Genres: [Comedy]
Cast: 

ID: tt0100740
Title: Tales from the Darkside: The Movie (1990)
Summary: From the depths of four twisted minds.. The first segment features an animated mummy stalking selected student victims; the second tale tells the story of a "cat from hell" who cannot be killed and leaves a trail of victims behind it; the third story is about a man who witnesses a bizarre killing and promises never to tell what he saw and the "in-between" bit is the story of a woman preparing to cook her newspaper boy for supper.
Genres: [Thriller, Comedy, Fantasy, Horror]
Cast: Christian Slater as Andy (segment "Lot 249"), Steve Buscemi as Bellingham (segment "Lot 249"), Julianne Moore as Susan (segment "Lot 249"), William Hickey as Drogan (segment "Cat From Hell"), Alice Drummond as Carolyn (segment "Cat From Hell"), James Remar as Preston (segment "Lover's Vow")

ID: tt0073747
Title: The Stepford Wives (1975)
Summary: Something strange is happening in the town of Stepford.. The urban aspirant photographer Joanna Eberhart moves from Manhattan to Stepford, Connecticut with her family. Her husband Walter Eberhart decided to live in a calm suburb, but Joanna did not like the neighborhood with beautiful and perfect housewives. She becomes friend of Bobbie Markowe and Charmaine Wimperis, and when they change Joanna tries to escape.
Genres: [Horror, Mystery, Science Fiction]
Cast: Mary Stuart Masterson as Kim Eberhart

ID: tt0134067
Title: The Rugrats Movie (1998)
Summary: . Tommy faces responsibility when Dil, his new baby brother, is born. As with all newborns, the child becomes a bane to Tommy and the rest of his gang. They decide to return Dil to where he came from, the hospital, but they get lost along the way. Can they find their way home and can Tommy and Dil learn to get along?
Genres: [Animation, Family]
Cast: Whoopi Goldberg as Ranger Margaret (voice), David Spade as Ranger Frank (voice), Tim Curry as Rex Pester (voice)

ID: tt0087344
Title: Godzilla 1985 (1984)
Summary: Your favorite fire breathing monster... like you've never seen him before!. Originally released in Japan as "The Return of Godzilla"  in 1984, this is the heavily re-edited, re-titled "Godzilla 1985".  Adding in new footage of Raymond Burr, this 16th Godzilla film ignores all previous sequels and serves as a direct follow-up to the 1956 "Godzilla King of the Monsters", which also featured scenes with Burr edited into 1954's "Godzilla". This film restores the darker tone of the original, as we witness the nuclear destruction of giant lizard terrorizing Japan.
Genres: [Action, Drama, Horror, Science Fiction, Thriller]
Cast: 

ID: tt0091541
Title: The Money Pit (1986)
Summary: For everyone who's ever been deeply in Love or deeply in debt.. After being evicted from their Manhattan apartment, a couple buys what looks like the home of their dreams – only to find themselves saddled with a bank-account-draining nightmare. Struggling to keep their relationship together as their rambling mansion falls to pieces around them, the two watch in hilarious horror as everything – including the kitchen sink – disappears into the 'Money Pit'.
Genres: [Comedy, Romance]
Cast: Tom Hanks as Walter Fielding, Jr., Joe Mantegna as Art Shirk, Philip Bosco as Curly, Josh Mostel as Jack Schnittman, Frankie Faison as , Michael Jeter as 

ID: tt0124198
Title: Very Bad Things (1998)
Summary: They've been bad. Very bad.. Kyle Fisher has one last night to celebrate life as a single man before marrying Laura, so he sets out to Vegas with four of his best buddies. But a drug and alcohol filled night on the town with a stripper who goes all the way, turns into a cold night in the desert with shovels when the stripper goes all the way into a body bag after dying in their bathroom. And that's just the first of the bodies to pile up before Kyle can walk down the aisle...
Genres: [Comedy, Crime, Thriller]
Cast: Cameron Diaz as Laura Garrety, Christian Slater as Robert Boyd, Jeremy Piven as Michael Berkow

ID: tt0120324
Title: A Simple Plan (1998)
Summary: Sometimes good people do evil things.. Captivated by the lure of sudden wealth, the quiet rural lives of two brothers erupt into conflicts of greed, paranoia and distrust when over $4 million in cash is discovered at the remote site of a downed small airplane. Their simple plan to retain the money while avoiding detection opens a Pandora's box when the fear of getting caught triggers panicked behavior and leads to virulent consequences
Genres: [Drama, Crime, Thriller]
Cast: Bill Paxton as Hank Mitchell, Bridget Fonda as Sarah Mitchell, Billy Bob Thornton as Jacob Mitchell

ID: tt0089961
Title: Santa Claus: The Movie (1985)
Summary: Guess Who's Coming To Town!. The first half of this film, set hundreds of years ago, shows how the old man who eventually became Santa Claus was given immortality and chosen to deliver toys to all the children of the world. The second half moves into the modern era, in which Patch, the head elf, strikes out on his own and falls in with an evil toy manufacturer who wants to corner the market and eliminate Santa Claus.
Genres: [Family, Fantasy, Adventure, Science Fiction, Comedy]
Cast: John Lithgow as B.Z.

ID: tt0089370
Title: The Jewel of the Nile (1985)
Summary: When the going gets tough, the tough get going.. Joan Wilder is thrust back into a world of murder, chases, foreign intrigue... and love. This time out she's duped by a duplicitous Arab dignitary who brings her to the Middle East, ostensibly to write a book about his life. Of course he's up to no good, and Joan is just another pawn in his wicked game. But Jack Colton and his sidekick Ralph show up to help our intrepid heroine save the day.
Genres: [Action, Adventure, Comedy, Romance]
Cast: Michael Douglas as Jack Colton, Kathleen Turner as Joan Wilder, Danny DeVito as Ralph, Holland Taylor as Gloria

ID: tt0091653
Title: Nothing in Common (1986)
Summary: It's a comedy. And a drama. Just like life.. On his way up the corporate ladder, David Basner confronts his greatest challenge: his father.
Genres: [Drama, Comedy, Romance]
Cast: Tom Hanks as David Basner, Héctor Elizondo as Charlie Gargas

ID: tt0097958
Title: National Lampoon's Christmas Vacation (1989)
Summary: Yule crack up.. It's Christmas time and the Griswolds are preparing for a family seasonal celebration, but things never run smoothly for Clark, his wife Ellen and their two kids. Clark's continual bad luck is worsened by his obnoxious family guests, but he manages to keep going knowing that his Christmas bonus is due soon.
Genres: [Comedy]
Cast: Chevy Chase as Clark Griswold, Beverly D'Angelo as Ellen Smith Griswold, Randy Quaid as Cousin Eddie, Juliette Lewis as Audry Griswold, William Hickey as Uncle Lewis, Brian Doyle-Murray as Mr. Frank Shirley

ID: tt0129290
Title: Patch Adams (1998)
Summary: Laughter is contagious. Meet Patch Adams, a doctor who doesn't look, act or think like any doctor you've met before. For Patch, humor is the best medicine, and he's willing to do just anything to make his patients laugh - even if it means risking his own career.
Genres: [Comedy, Drama]
Cast: Robin Williams as Hunter 'Patch' Adams, Philip Seymour Hoffman as Mitch Roman, Bob Gunton as Dean Walcott, Peter Coyote as Bill Davis, Michael Jeter as Rudy

ID: tt0120699
Title: The Hi-Lo Country (1998)
Summary: A woman like Mona can drive men to extremes.. An intimate story of the enduring bond of friendship between two hard-living men, set against a sweeping backdrop: the American West, post-World War II, in its twilight. Pete and Big Boy are masters of the prairie, but ultimately face trickier terrain: the human heart.
Genres: [Drama, Action, Western, Romance]
Cast: Woody Harrelson as Big Boy Matson, Patricia Arquette as Mona, John Diehl as Les Birk

ID: tt0093072
Title: The Garbage Pail Kids Movie (1987)
Summary: It doesn't get any grosser than this!. Seven disgusting kids but nevertheless of interesting personality are being made of the green mud coming out of garbage can. Once alive their master gives them rules to obey although they think that life is funnier without following stupid regulations like no television or no candy. Naturally this will cause some conflicts.
Genres: [Adventure, Science Fiction, Comedy]
Cast: 

ID: tt0093075
Title: The Gate (1987)
Summary: Pray it's not too late.. Three young children accidentally release a horde of nasty, pint-sized demons from a hole in a suburban backyard. What follows is a classic battle between good and evil as the three kids struggle to overcome a nightmarish hell that is literally taking over the Earth.
Genres: [Horror, Fantasy]
Cast: 

ID: tt0097368
Title: The Fly II (1989)
Summary: Like Father Like Son. Martin Brundle, born of the human/fly, is adopted by his father's place of employment (Bartok Inc.) while the employees simply wait for his mutant chromosomes to come out of their dormant state.
Genres: [Horror, Science Fiction, Thriller]
Cast: Eric Stoltz as Martin Brundle

ID: tt0090917
Title: Deadly Friend (1986)
Summary: There's no one alive who'll play with the girl next door.. Paul is a new kid in town with a robot named "BB". He befriends Samantha and the three of them have a lot of good times together. That is, until Samantha's abusive father throws her down some stairs and kills her. In an effort to save her life, Paul implants BB's computer brain into Samantha's human brain.
Genres: [Horror, Science Fiction]
Cast: Charles Fleischer as BB

ID: tt0091187
Title: Heartbreak Ridge (1986)
Summary: ...the scars run deep.. A hard-nosed, hard-living Marine gunnery sergeant clashes with his superiors and his ex-wife as he takes command of a spoiled recon platoon with a bad attitude.
Genres: [Action, Comedy, Drama, War]
Cast: Clint Eastwood as Gunnery Sgt. Tom 'Gunny' Highway

ID: tt0054167
Title: Peeping Tom (1960)
Summary: What made this the most diabolical murder weapon ever used?. Loner Mark Lewis works at a film studio during the day and, at night, takes racy photographs of women. Also he's making a documentary on fear, which involves recording the reactions of victims as he murders them. He befriends Helen, the daughter of the family living in the apartment below his, and he tells her vaguely about the movie he is making. She sneaks into Mark's apartment to watch it and is horrified by what she sees -- especially when Mark catches her.
Genres: [Horror, Thriller]
Cast: 

ID: tt0120784
Title: Payback (1999)
Summary: No More Mr. Nice Guy.. With friends like these, who needs enemies? That's the question bad guy Porter is left asking after his wife and partner steal his heist money and leave him for dead -- or so they think. Five months and an endless reservoir of bitterness later, Porter's partners and the crooked cops on his tail learn how bad payback can be.
Genres: [Drama, Action, Thriller, Crime]
Cast: Mel Gibson as Porter, Kris Kristofferson as Bronson, David Paymer as Arthur Stegman, John Glover as Phil, James Coburn as Justin Fairfax

ID: tt0138987
Title: 20 Dates (1998)
Summary: A comedy so real you'll think it's fiction.. Myles is divorced in L.A. He wants a love life and a film career. So he decides to go on 20 dates and find true love in front of a camera, making his first feature. His patient agent, Richard, finds a $60,000 investor, the shadowy Elie. Myles starts his search, sometimes telling his date she's being filmed, sometimes not. Elie wants sex and titillation, Myles wants it "real." Myles regularly talks with his old film teacher, Robert McKee, who wonders if love is possible in modern life. Half-way through the 20 dates, Myles meets Elisabeth; she's everything he desires and she likes him. Can he finish the 20 dates, satisfy Elie, and complete his film without losing Elisabeth?
Genres: [Romance, Comedy]
Cast: 

ID: tt0124298
Title: Blast from the Past (1999)
Summary: She'd never met anyone like him. He's never met anyone... Period.. Following a bomb scare in the 1960s that locked the Webers into their bomb shelter for 35 years, Adam now ventures forth into Los Angeles to obtain food and supplies for his family, and a non-mutant wife for himself.
Genres: [Comedy, Romance]
Cast: Brendan Fraser as Adam Webber, Christopher Walken as Calvin Webber, Sissy Spacek as Helen Thomas Webber

ID: tt0070334
Title: The Long Goodbye (1973)
Summary: Nothing says goodbye like a bullet.... Detective Philip Marlowe tries to help a friend who is accused of murdering his wife.
Genres: [Thriller, Crime, Drama, Mystery]
Cast: Elliott Gould as Philip Marlowe, Arnold Schwarzenegger as Hood in Augustine's office

ID: tt0085333
Title: Christine (1983)
Summary: How do you kill something that can't possibly be alive?. Geeky student Arnie Cunningham falls for Christine, a rusty 1958 Plymouth Fury, and becomes obsessed with restoring the classic automobile to her former glory. As the car changes, so does Arnie, whose newfound confidence turns to arrogance behind the wheel of his exotic beauty. Arnie's girlfriend Leigh and best friend Dennis reach out to him, only to be met by a Fury like no other.
Genres: [Horror]
Cast: Robert Prosky as Will Darnell, Harry Dean Stanton as Rudolph Junkins, Kelly Preston as Roseanne

ID: tt0071110
Title: Airport 1975 (1974)
Summary: Something hit us... The crew is dead... Help us, please, please help us!. "Something hit us...the crew is dead...help us, please, please help us!" With these terrifying words, 22 of Hollywood's greatest stars find themselves aboard a pilotless jumbo jet headed on a collision course with destruction in the nerve chilling sequel to the greatest disaster movie ever made.
Genres: [Action, Adventure, Drama, Thriller]
Cast: Charlton Heston as Alan Murdock, George Kennedy as Joe Patroni

ID: tt0065462
Title: Beneath the Planet of the Apes (1970)
Summary: An army of civilized apes...A fortress of radiation-crazed super humans...Earth's final battle is about to begin - Beneath the atomic rubble of what was once the city of New York!. Astronaut Brent is sent to rescue Taylor but crash lands on the Planet of the Apes, just like Taylor did in the original film. Taylor has disappeared into the Forbidden Zone so Brent and Nova try to follow and find him. He discovers a cult of humans that fear the Apes' latest military movements and finds himself in the middle. Tension mounts to a climactic battle between ape and man deep in the bowels of the planet.
Genres: [Adventure, Science Fiction, Mystery]
Cast: Charlton Heston as Taylor

ID: tt0157208
Title: The School of Flesh (1998)
Summary: Lessons in love, sex and betrayal.. In Paris, Dominique, a middle-aged fashion professional, solicits the services of the handsome Quentin , a bisexual bartender and prostitute who is 15 years her junior. After they first sleep together, their business transaction becomes transformed into a passionate love affair. However, the couple's romance becomes an ugly power struggle when social class and age distinctions begin to bubble toward the surface.
Genres: [Romance, Drama, Mystery]
Cast: 

ID: tt0120646
Title: The Deep End of the Ocean (1999)
Summary: The search for her son was over. The search for her family was just beginning.. Michelle Pfeiffer is ferocious in the role of a desperate mother whose 3-year-old son disappears during her high school reunion. Nine years later, by chance, he turns up in the town in which the family has just relocated. Based on Jacquelyn Mitchard's best-selling novel (an Oprah book club selection), the movie effectively presents the troubling dynamics that exist between family members who've suffered such an unsettling loss.
Genres: [Drama, Mystery]
Cast: Michelle Pfeiffer as Beth Cappadora, Whoopi Goldberg as Candy Bliss, Lucinda Jenney as Laurie

ID: tt0144814
Title: The Rage: Carrie 2 (1999)
Summary: Looks Can Kill. After the suicide of her only friend, Rachel has never felt more on the outside. The one person who reached out to her, Jessie, also happens to be part of the popular crowd that lives to torment outsiders like her. But Rachel has something else that separates her from the rest, a secret amazing ability to move things with her mind. Sue Snell, the only survivor of Carrie White's rampage twenty-two years ago, may hold the key to helping Rachel come to terms with her awesome, but unwanted power. But as Rachel slowly learns to trust, a terrible trap is being laid for her. And making her angry could prove to be fatal.
Genres: [Horror, Thriller, Science Fiction]
Cast: 

ID: tt0187819
Title: Doug's 1st Movie (1999)
Summary: You'll Dig Doug!. Doug and his pal Skeeter set's out to find the monster of Lucky Duck Lake. Though things get really out of hand when some one blurts out that the monster is real.
Genres: [Animation, Family, Comedy]
Cast: 

ID: tt0116932
Title: Love, etc. (1996)
Summary: . A triangle: love, obsession, and choice. Pierre, a ladies' man who has little cash and no fixed residence, describes his best friend Benoît as the world's oldest 32-year-old. The shy, well-employed Benoît's life changes when he answers the personal ad of Marie, a 25-year-old who restores paintings. He's attracted to her and she likes his steady calm and his honest attention. They're soon a couple, and they include Pierre in their dinners, outings, and trips. What will happen when Pierre realizes that he too is in love with Marie?
Genres: []
Cast: 

ID: tt0119219
Title: Goodbye Lover (1998)
Summary: No one's ever been so good at being so bad.. Police investigate when a man having an affair with his brother's wife disappears suddenly.
Genres: [Thriller, Comedy, Crime, Mystery]
Cast: Patricia Arquette as Sandra Dunmore, Dermot Mulroney as Jake Dunmore, Mary-Louise Parker as Peggy Blane

ID: tt0188996
Title: Photographer (1998)
Summary: . In 1987, colour slides were found in a second hand book store in Vienna which turned out to be a collections of photographs taken in the Lodz ghetto by the Nazis' chief accountant. Walter Genewein boosted productivity in the ghetto while keeping costs down, a policy which led to the Lodz ghetto surviving much longer than any other in Poland. He recorded what he considered to be the subhuman aspect of the Jewish workers and he was concerned only with the technical quality of his photos.  Director Dariusz Jabłoński's prize-winning film uses the photographs in a different way. He recreates for us the suffering of inmates, giving a compassionate picture of that it was like to be trapped in the ghetto. (Storyville)
Genres: [TV Movie, History, Documentary, War]
Cast: 

ID: tt0037098
Title: The Mummy's Curse (1944)
Summary: HANDS CREEPING LIKE COBRAS! EYES CRAWLING WITH MADNESS! THE MUMMY RUNS AMOK!. After being buried in quicksand for the past 25 years, Kharis is set free to roam the rural bayous of Louisiana, as is the soul of his beloved Princess Ananka, still housed in the body of Amina Mansouri, who seeks help and protection at a swamp draining project.
Genres: [Horror]
Cast: Lon Chaney Jr. as Kharis

ID: tt0035899
Title: Frankenstein Meets the Wolf Man (1943)
Summary: . Graverobbers open the grave of the Wolfman and awake him. He doesn't like the idea of being immortal and killing people when the moon is full, so he tries to find Dr. Frankenstein, in the hopes that the Dr. can cure him, but Frankenstein is dead and only his Monster is alive and this one wants to live, not to die like the Wolfman.
Genres: [Fantasy, Horror, Science Fiction]
Cast: Lon Chaney Jr. as Lawrence Stewart Talbot, the wolfman, Bela Lugosi as The Frankenstein Monster

ID: tt0084156
Title: It Came from Hollywood (1982)
Summary: We're going to make you LAUGH.. Dan Ackroyd, John Candy, Gilda Radner and Cheech and Chong present this compilation of classic bad films from the 50's, 60's and 70's. Special features on gorilla pictures, anti-marijuana films and a special tribute to the worst film maker of all-time, Ed Wood.
Genres: [Comedy, Documentary]
Cast: Dan Aykroyd as Himself, John Candy as Himself, Cheech Marin as Himself

ID: tt0052151
Title: Run Silent, Run Deep (1958)
Summary: Gable and Lancaster make the seas boil in the battle adventure that hits like a torpedo!. The captain of a submarine sunk by the Japanese during WWII is finally given a chance to skipper another sub after a year of working a desk job. His singleminded determination for revenge against the destroyer that sunk his previous vessel puts his new crew in unneccessary danger.
Genres: [Drama, War]
Cast: Jack Warden as Yeoman 1st Class Mueller

ID: tt0139809
Title: The Thirteenth Floor (1999)
Summary: Question reality. You can go there even though it doesn't exist.. Computer scientist Hannon Fuller has discovered something extremely important. He's about to tell the discovery to his colleague, Douglas Hall, but knowing someone is after him, the old man leaves a letter in his computer generated parallel world that's just like the 30's with seemingly real people with real emotions.
Genres: [Thriller, Science Fiction, Mystery]
Cast: Armin Mueller-Stahl as Hannon Fuller/Grierson, Vincent D'Onofrio as Jason Whitney/Jerry Ashton

ID: tt0128278
Title: Instinct (1999)
Summary: . Dr. Ethan Powell, an anthropologist, is in Africa studying apes when he is lost for two years. When he is found, he kills 3 men and puts 2 in the hospital. Cuba Gooding's character is a psychiatrist who wants to take up the task of trying to get Dr. Powell to speak again and maybe even stand judgment at a trial for his release from prison of mental cases. Along the way, Cuba has to deal with also helping the mental patients that are being abused and neglected. In this process Cuba learns a few things about himself and life, and so does Anthony Hopkins character, Dr. Powell.
Genres: [Drama, Mystery, Thriller]
Cast: Anthony Hopkins as Dr. Ethan Powell, Cuba Gooding Jr. as Dr. Theo Caulder, Donald Sutherland as Ben Hillard, George Dzundza as Dr. John Murray, Rex Linn as Gurad Alan

ID: tt0141105
Title: Free Enterprise (1998)
Summary: Love long and party. Young filmmakers trying to hawk a movie titled "Bradykillers" about a serial killer who goes after victims Marcia, Jan, and Cindy meet their screen idol, William Shatner. The two young men, who idolize him and in their fantasies have seen him as a shadowy fairy godfather figure, are alarmed at the reality of the middle-aged non-Captain Kirk man that they meet.
Genres: [Comedy, Romance]
Cast: William Shatner as Bill

ID: tt0130827
Title: Run Lola Run (1998)
Summary: Every second of every day you're faced with a decision that can change your life.. Lola receives a phone call from her boyfriend Manni. He lost 100,000 DM in a subway train that belongs to a very bad guy. She has 20 minutes to raise this amount and meet Manni. Otherwise, he will rob a store to get the money. Three different alternatives may happen depending on some minor event along Lola's run.
Genres: [Action, Drama, Thriller]
Cast: 

ID: tt0158983
Title: South Park: Bigger, Longer & Uncut (1999)
Summary: UH-OH.. When the four boys see an R-rated movie featuring Canadians Terrance and Philip, they are pronounced "corrupted", and their parents pressure the United States to wage war against Canada.
Genres: [Animation, Comedy, Music]
Cast: George Clooney as Dr. Gouache, Minnie Driver as Brooke Shields, Brent Spiner as Conan O'Brien, Eric Idle as Dr. Vosknocker

ID: tt0163651
Title: American Pie (1999)
Summary: There's nothing like your first piece.. At a high-school party, four friends find that losing their collective virginity isn't as easy as they had thought. But they still believe that they need to do so before college. To motivate themselves, they enter a pact to all "score." by their senior prom.
Genres: [Comedy, Romance]
Cast: Eugene Levy as Jim's Father

ID: tt0185937
Title: The Blair Witch Project (1999)
Summary: The scariest movie of all time is a true story.. In October of 1994 three student filmmakers disappeared in the woods near Burkittsville, Maryland, while shooting a documentary. A year later their footage was found.
Genres: [Horror, Mystery]
Cast: 

ID: tt0157503
Title: Drop Dead Gorgeous (1999)
Summary: The battle between the good and the bad is bound to get ugly.. In a small Minnesota town, the annual beauty pageant is being covered by a TV crew. Former winner Gladys Leeman wants to make sure her daughter follows in her footsteps; explosions, falling lights, and trailer fires prove that. As the Leemans are the richest family in town, the police are pretty relaxed about it all. Despite everything, main rival (but sweet) Amber Atkins won't give up without a fight.
Genres: [Comedy]
Cast: Kirsten Dunst as Amber Atkins, Ellen Barkin as Annette Atkins, Allison Janney as Loretta, Matt Malloy as John Dough, Judge #1

ID: tt0171363
Title: The Haunting (1999)
Summary: Some houses are born bad.. Dr. David Marrow invites Nell Vance, and Theo and Luke Sanderson to the eerie and isolated Hill House to be subjects for a sleep disorder study. The unfortunate guests discover that Marrow is far more interested in the sinister mansion itself – and they soon see the true nature of its horror.
Genres: [Horror, Thriller, Fantasy, Mystery]
Cast: Liam Neeson as Dr. David Marrow, Lili Taylor as Eleanor 'Nell' Vance, Bruce Dern as Mr. Dudley

ID: tt0049406
Title: The Killing (1956)
Summary: ...Like No Other Picture Since "Scarface" and "Little Caesar"!. The Killing was Stanley Kubrick’s first film with a professional cast and the first time he achieved public recognition as the unconventional director he’s now known for. The story is of ex-prisoners who plan to set up a racetrack so they can live a life without monetary worries. One of the more exceptional films of the 1950’s.
Genres: [Drama, Action, Thriller, Crime]
Cast: Arthur Tovey as Racetrack Cashier (uncredited)

ID: tt0056193
Title: Lolita (1962)
Summary: How did they ever make a movie of .... Humbert Humbert is a middle-aged British novelist who is both appalled by and attracted to the vulgarity of American culture. When he comes to stay at the boarding house run by Charlotte Haze, he soon becomes obsessed with Lolita, the woman's teenaged daughter.
Genres: [Drama, Romance]
Cast: 

ID: tt0088939
Title: The Color Purple (1985)
Summary: It's about life. It's about love. It's about us.. An epic tale spanning forty years in the life of Celie (Whoopi Goldberg), an African-American woman living in the South who survives incredible abuse and bigotry.  After Celie's abusive father marries her off to the equally debasing "Mister" Albert Johnson (Danny Glover), things go from bad to worse, leaving Celie to find companionship anywhere she can.  She perseveres, holding on to her dream of one day being reunited with her sister in Africa.  Based on the novel by Alice Walker.
Genres: [Drama]
Cast: Whoopi Goldberg as Celie, Danny Glover as Albert, Dana Ivey as Miss Millie, Laurence Fishburne as Swain

ID: tt0093051
Title: From the Hip (1987)
Summary: Getting To The Top Means Working Like A Dog!. Apprentice lawyer Robin Weathers turns a civil suit into a headline grabbing charade. He must reexamine his scruples after his shenanigans win him a promotion in his firm, and he must now defend a college professor who is appearantly guilty of murder.
Genres: [Comedy, Drama]
Cast: John Hurt as Douglas Benoit

ID: tt0094293
Title: Wanted: Dead or Alive (1987)
Summary: Nick Randall is a loner. A legend. A bounty hunter. He's the best there is at the job he hates.. This movie features a character who is supposed to be the descendant of the character played Steve McQueen in the television series of the same name. And like McQueen's Josh Randall, Hauer's Nick Randall is also a bounty. But also an ex-CIA operative, who is asked by his former employer to help them track down a terrorrist, Malak Al Rahim, who is in the country, and has already made a move. But he is also looking for Randall, and the people, whom Randall is working for, is telling Malak, where he can find Randall.
Genres: [Action, Thriller, Crime, Drama]
Cast: 

ID: tt0131325
Title: Bowfinger (1999)
Summary: The con is on.. On the verge of bankruptcy and desperate for his big break, aspiring filmmaker Bobby Bowfinger concocts a crazy plan to make his ultimate dream movie. Rallying a ragtag team that includes a starry-eyed ingenue, a has-been diva and a film studio gofer, he sets out to shoot a blockbuster featuring the biggest star in Hollywood, Kit Ramsey -- only without letting Ramsey know he's in the picture.
Genres: [Comedy]
Cast: Steve Martin as Bobby Bowfinger, Eddie Murphy as Kit Ramsey | Jeffernson 'Jiff' Ramsey, Heather Graham as Daisy, Christine Baranski as Carol, Terence Stamp as Terry Stricter, Robert Downey Jr. as Jerry Renfro

ID: tt0168987
Title: Better Than Chocolate (1999)
Summary: A delicious romantic comedy.. Not long after moving into her own place, Maggie finds herself with two unsolicited roommates: her recently divorced mother, Lila, and her young brother. The timing is especially bad, considering Maggie has fallen hard for an attractive woman, Kim, only hours before they move in. What could be a nonissue becomes increasingly complicated -- since Maggie's family is unaware of her sexual orientation, and Maggie is not open to sharing that information.
Genres: [Drama, Romance]
Cast: 

ID: tt0119614
Title: Marcello Mastroianni: I Remember (1997)
Summary: . In 1996, Marcello Mastroianni talks about life as an actor. It's an anecdotal and philosophical memoir, moving from topic to topic, fully conscious of a man "of a certain age" looking back. He tells stories about Fellini and De Sica's direction, of using irony in performances, of constantly working (an actor tries to find himself in characters). He's diffident about prizes, celebrates Rome and Paris, salutes Naples and its people. He answers the question, why make bad films; recalls his father and grandfather, carpenters, his mother, deaf in her old age, and his brother, a film editor; he's modest about his looks. In repose, time's swift passage holds Mastroianni inward gaze.
Genres: [Documentary]
Cast: 

ID: tt0089670
Title: National Lampoon’s European Vacation (1985)
Summary: For over two thousand years, Europe has survived many great disasters. Now for the real test. Chevy Chase & his family are coming from America!. The Griswalds win a vacation to Europe on a game show, and thus pack their bags for the continent. They do their best to catch the flavor of Europe, but they just don't know how to be be good tourists. Besides, they have trouble taking holidays in countries where they CAN speak the language.
Genres: [Adventure, Comedy, Romance]
Cast: Chevy Chase as Clark Griswold, Beverly D'Angelo as Ellen Griswold, Eric Idle as The Bike Rider

ID: tt0176269
Title: Universal Soldier: The Return (1999)
Summary: Prepare to become obsolete. Luc Deveraux, the heroic former Universal Soldier, is about to be thrown into action once again. When Seth (Michael Jai White), the supercomputer controlled ultra-warrior, decides to take revenge and destroy its creators, only Luc can stop it. All hell breaks loose as Luc battles Seth and a deadly team of perfect soldiers in a struggle that pits man against machine and good against evil.
Genres: [Action, Science Fiction]
Cast: Jean-Claude Van Damme as Luc Devereux, Xander Berkeley as Dr. Dylan Cotner, Daniel von Bargen as General Radford

ID: tt0105698
Title: Universal Soldier (1992)
Summary: The future has a bad attitude.. An American soldier who had been killed during the Vietnam War is revived 25 years later by the military as a semi-android, UniSols, a high-tech soldier of the future. After the failure of the initiative to erase all the soldier's memories, he begins to experience flashbacks that are forcing him to recall his past.
Genres: [Thriller, Action, Science Fiction, Crime]
Cast: Jean-Claude Van Damme as Luc Devreux/GR44, Rance Howard as John Devreux

ID: tt0188863
Title: Love Stinks (1999)
Summary: . A movie about a relationship...that's worse than yours. Seth (Stewart), a sitcom writer-producer, meets Chelsea (Wilson), an interior decorator, at his best friend's (Bellamy) wedding. He's immediately sexually attracted to her while she's instantly attracted to his single-ness. They both ditch their wedding dates and start their own date that same night. The two become a couple, appearing very happy until after a couple of years of postponing a marriage proposal. When Chelsea realizes that Seth wants to remain single and together, she becomes quite bitter. In the next hour of the movie, the two engage in behavior that makes the War of the Roses look like child's play.
Genres: [Comedy, Romance]
Cast: Bridgette Wilson as Chelsea Turner, Colleen Camp as Monica Harris

ID: tt0119692
Title: With Friends Like These... (1998)
Summary: . Four small-time two-bit character actors, all close friends, are competing for same important part in the next Martin Scorsese mob film.
Genres: [Horror, Science Fiction]
Cast: Bill Murray as Maurice Melnick, David Strathairn as , Beverly D'Angelo as 

ID: tt0163185
Title: Rosie (1998)
Summary: . In a juvenile lockup, a new ward answers questions: she's Rosie, 13, no parents, a sister Irene, a brother Michel. In flashbacks we find out what happened. She lives with Irene, who's 27, whom Rosie knows is in fact her mother, but that's their secret. Irene's brother Michel, unemployed, a compulsive gambler, comes to stay with them. Around then, Irene meets Bernard; they come to care for each other. This leaves Rosie without attention, so she puts all her adolescent hopes and romantic fantasies into a relationship with Jimi, a good looking kid she sees on a bus. Is it adventures with Jimi that land her in juvie? Once she's there, why doesn't he answer her letters?
Genres: []
Cast: 

ID: tt0160216
Title: A Dog Of Flanders (1999)
Summary: . Poor but happy, young Nello and his grandfather live alone, delivering milk as a livelihood, in the outskirts of Antwerp, a city in Flanders (the Flemish or Dutch-speaking part of modern-day Belgium). They discover a beaten dog (a Bouvier, a large sturdy dog native to Flanders) and adopt it and nurse it back to health, naming it Patrasche, the middle name of Nello's mother Mary, who died when Nello was very young. Nello's mother was a talented artist, and like his mother, he delights in drawing, and his friend Aloise is his model and greatest fan and supporter.
Genres: [Drama, Family]
Cast: Jack Warden as Jehan, Jon Voight as Michel, Bruce McGill as William

ID: tt0119586
Title: Lucie Aubrac (1997)
Summary: . A love story or a tale of the resistance, this poignant movie tells both the haunting story of a French resistance cell in Lyon but also the love of Lucie Aubrac for her husband...
Genres: [Drama]
Cast: Daniel Auteuil as Raymond Aubrac

ID: tt0163579
Title: Chill Factor (1999)
Summary: This Fall, Action is served on the rocks, with a twist.. A store clerk and an ice cream truck driver are thrown together when a dying scientist entrusts them with a deadly chemical kept in ice. This chemical will kill every living thing once it melts. They have to take the chemical codenamed 'Elvis' to the next nearest military base while being chased by terrorists who want it to hold the country for ransom.
Genres: [Action, Comedy, Thriller]
Cast: Cuba Gooding Jr. as Arlo, Skeet Ulrich as Tim Mason, David Paymer as Dr. Richard Long, Kevin J. O'Connor as Telstar

ID: tt0125971
Title: Outside Providence (1999)
Summary: Tim's getting a future... whether he likes it or not!. In this coming-of-age comedy, Tim Dunphy is leading a go-nowhere existence, spending his days smoking pot and hanging out with his best friend, Drugs Delaney. But Tim's lazy days of getting high are jettisoned after a brush with the law convinces his blue-collar dad to send him to a Connecticut prep school. The one saving grace of the new school is Jane, a fellow student Tim falls for immediately.
Genres: [Romance, Comedy, Drama]
Cast: Alec Baldwin as Old Man Dunphy, Richard Jenkins as Barney, George Wendt as Joey, George Martin as Dean Mort

ID: tt0157183
Title: West Beyrouth (1998)
Summary: . In April, 1975, civil war breaks out; Beirut is partitioned along a Moslem-Christian line. Tarek is in high school, making Super 8 movies with his friend, Omar. At first the war is a lark: school has closed, the violence is fascinating, getting from West to East is a game. His mother wants to leave; his father refuses. Tarek spends time with May, a Christian, orphaned and living in his building. By accident, Tarek goes to an infamous brothel in the war-torn Olive Quarter, meeting its legendary madam, Oum Walid. He then takes Omar and May there using her underwear as a white flag for safe passage. Family tensions rise. As he comes of age, the war moves inexorably from adventure to tragedy.
Genres: [Romance, Drama, Comedy]
Cast: 

ID: tt0045251
Title: Othello (1951)
Summary: Orson Welles' magnificent screening of Shakespeare's immortal tragedy. Desdemona, daughter of a Venetian aristocrat, elopes with Moorish military hero Othello, to the great resentment of Othello's envious underling Iago. Alas, Iago knows Othello's weakness, and with chilling malice works on him with but too good effect...
Genres: [Drama, Romance]
Cast: Joseph Cotten as Senator (uncredited)

ID: tt0088178
Title: Stop Making Sense (1984)
Summary: Why stop making sense? Why a movie? Why a big suit? Where do the odd movements come from? What will the band do next?. David Byrne walks onto the stage and does a solo "Psycho Killer". Jerry Harrison, Tina Weymouth and Chris Frantz join him for two more songs. The crew is busy, still setting up. Then, three more musicians and two back-up singers join the band. Everybody sings, plays, harmonizes, dances, and runs. In this concert film, the Talking Heads hardly talk, don't stop, and always make sense.
Genres: [Documentary, Music]
Cast: 

ID: tt0127296
Title: Splendor (1999)
Summary: Explore the possibilities.... Veronica is a white-bread beauty searching for a good man in Los Angeles. While slam dancing at a Halloween rave, she meets Abel, a sensitive poet. Then she meets Zed, a supersexy tattooed drummer with incredible biceps. Who will she choose? Does she go for true love or cheap sex? She can't decide so she chooses both. But after managing to nurture a picture-perfect threesome, along comes Ernest, a rich movie director with deep baby blues that sweep Veronica off her feet. What's a girl to do now?
Genres: [Comedy]
Cast: 

ID: tt0173390
Title: Sugar Town (1999)
Summary: . Look at the lives of struggling L.A. scene rock stars follows main character, Gwen, on her quest for the top. Working as an assistant to a film production designer, she tries to steal her boy friend who is a music producer by offering sexual favors. The producer meanwhile is trying to orchestrate a comeback for a former glam band comprised of Michael Des Barres, John Taylor and Martin Kemp. Rosanna Arquette plays the former movie star wife of the lead singer, who is fretting because she has just been offered the role as the mother of one of the new ingenious. Beverly D'Angelo also shows up as a millionairess who agrees to bankroll the group, but only if she gets a roll in the hay with the lead singer. All of the career problems, including drug proclivity, are represented in this film.
Genres: [Comedy]
Cast: Lucinda Jenney as Kate

ID: tt0089175
Title: Fright Night (1985)
Summary: There are some very good reasons to be afraid of the dark.. Nobody believes teenager Charley Brewster (William Ragsdale) when he discovers that his suave new neighbor, Jerry Dandrige (Chris Sarandon), is a vampire. So when the bloodsucker starts stalking Charley, he turns to has-been actor Peter Vincent (Roddy McDowall), famed for portraying a ghoul hunter. Unfortunately for the would-be vampire slayers, Dandrige has set his sights on Charley's girlfriend (Amanda Bearse) in this clever spoof of the horror genre.
Genres: [Horror]
Cast: Roddy McDowall as Peter Vincent, Art Evans as Detective Lennox

ID: tt0159421
Title: The Adventures of Elmo in Grouchland (1999)
Summary: The Good, The Bad and The Stinky.. Elmo loves his fuzzy, blue blanket, and would never let anything happen to it. However, a tug-of-war with his friend Zoe sends his blanket to a faraway land, and Elmo in hot pursuit. Facing life without his cherished blanket, Elmo musters all of his determination and courage and heads off on an action-packed rescue mission that plunges him into Grouchland-a place full of grouchy creatures, stinky garbage and the villainous Huxley. Along the way, Elmo learns an important lesson about sharing, realizing that he was selfish with his friend and responsible for what happened.
Genres: [Family]
Cast: Frank Oz as Bert / Grover / Cookie Monster (voice)

ID: tt0120188
Title: Three Kings (1999)
Summary: It's good to be King.. A group of American soldiers stationed in Iraq at the end of the Gulf War find a map they believe will take them to a huge cache of stolen Kuwaiti gold hidden near their base, and they embark on a secret mission that's destined to change everything.
Genres: [Action, Adventure, Comedy]
Cast: George Clooney as Archie Gates, Mark Wahlberg as Troy Barlow, Mykelti Williamson as Colonel Horn

ID: tt0162360
Title: Happy, Texas (1999)
Summary: Escaped convicts disguised as beauty pageant experts? This could get ugly. Two escaped convicts roll into the village of Happy, Texas, where they're mistaken for a gay couple who work as beauty pageant consultants. They go along with it to duck the police, but the local sheriff has a secret of his own.
Genres: [Comedy, Crime]
Cast: Steve Zahn as Wayne Wayne Wayne Jr., William H. Macy as Sheriff Chappy Dent, Jeremy Northam as Harry Sawyer, Ron Perlman as Marshal Nalhober, Illeana Douglas as Doreen Schaefer, Paul Dooley as The Judge

ID: tt2378428
Title: Romance (2012)
Summary: . She's as hot as Britney Spears. Hotter. She parties like Hollywood. She's way out of his league. Or that's the story he's sticking to.
Genres: [Romance, Drama, Comedy]
Cast: 

ID: tt0106664
Title: The Dark Half (1993)
Summary: There are very good reasons to be afraid of the dark.. Thad Beaumont is the author of a highly successful series of violent pulp thrillers written under the pseudonym of ‘George Stark’, but when he decides to ‘kill-off’ his alter-ego in a mock ceremony, it precipitates a string of sadistic murders matching those in his pulp novels, which are soon discovered to be the work of Stark himself. Looking like a maniacal version of his counterpart, Stark is not so willing to quit the writing game – even if it means coming after Thad's wife and their baby.
Genres: [Mystery, Horror]
Cast: Michael Rooker as Sheriff Alan Pangborn, Beth Grant as Shayla Beaumont

ID: tt0167427
Title: Superstar (1999)
Summary: Dare to dream.. Orphan Mary Katherine Gallagher, an ugly duckling at St. Monica High School, has a dream: to be kissed soulfully. She decides she can realize this dream if she becomes a superstar, so her prayers, her fantasies, and her conversations with her only friend focus on achieving super-stardom.
Genres: [Comedy, Family]
Cast: 

ID: tt0086200
Title: Risky Business (1983)
Summary: Time of your life, Huh Kid.. Meet Joel Goodson, an industrious, college-bound 17-year-old and a responsible, trustworthy son. However, when his parents go away and leave him home alone in the wealthy Chicago suburbs with the Porsche at his disposal he quickly decides he has been good for too long and it is time to enjoy himself. After an unfortunate incident with the Porsche Joel must raise some cash, in a risky way.
Genres: [Comedy, Romance]
Cast: Tom Cruise as Joel Goodsen, Sean Penn as Cameo (uncredited), Joe Pantoliano as Guido, Richard Masur as Rutherford

ID: tt0082979
Title: Reds (1981)
Summary: Not since Gone With The Wind has there been a great romantic epic like it!. A radical American journalist becomes involved with the Communist revolution in Russia and hopes to bring its spirit and idealism to the United States.
Genres: [Drama, History]
Cast: Warren Beatty as John Reed, Diane Keaton as Louise Bryant, Jack Nicholson as Eugene O'Neill, Paul Sorvino as Louis Fraina, M. Emmet Walsh as Speaker - Liberal Club, Gene Hackman as Pete Van Wherry, Miriam Margolyes as Woman writing in Notebook

ID: tt0033804
Title: The Lady Eve (1941)
Summary: Barbara Stanwyck has Henry Fonda Bewitched and Bewildered. Returning from a year up the Amazon studying snakes, the rich but unsophisticated Charles Pike meets con-artist Jean Harrington on a ship. They fall in love, but a misunderstanding causes them to split on bad terms. To get back at him, Jean disguises herself as an English lady, and comes back to tease and torment him.
Genres: [Comedy, Romance]
Cast: Henry Fonda as Charles Pike, Bert Moorhouse as Diner on Ship (uncredited)

ID: tt0038559
Title: Gilda (1946)
Summary: There NEVER was a woman like Gilda!. Just arrived in Argentina, small-time crooked gambler Johnny Farrell is saved from a gunman by sinister Ballin Mundson, who later makes Johnny his right-hand man. But their friendship based on mutual lack of scruples is strained when Mundson returns from a trip with a wife: the supremely desirable Gilda, whom Johnny once knew and learned to hate. The relationship of Johnny and Gilda, a battlefield of warring emotions, becomes even more bizarre after Mundson disappears...
Genres: [Drama, Romance, Thriller]
Cast: 

ID: tt0052225
Title: South Pacific (1958)
Summary: There is nothing you can name that is anything like... [South Pacific]. Can a girl from Little Rock find happiness with a mature French planter she got to know one enchanted evening away from the military hospital where she is a nurse? Or should she just wash that man out of her hair? Bloody Mary is the philosopher of the island and it's hard to believe she could be the mother of Liat who has captured the heart of Lt. Joseph Cable USMC. While waiting for action in the war in the South Pacific, sailors and nurses put on a musical comedy show. The war gets closer and the saga of Nellie Forbush and Emile de Becque becomes serious drama.
Genres: [Music, Romance, War]
Cast: 

ID: tt0085549
Title: Flashdance (1983)
Summary: When the dancer becomes the dance.. The popular 1980’s dance movie that depicts the life of an exotic dancer with a side job as a welder who true desire is to get into ballet school. It’s her dream to be a professional dancer and now is her chance. The film has a great soundtrack with an Oscar winning song.
Genres: [Drama, Music, Romance]
Cast: 

ID: tt0119256
Title: Hard Eight (1996)
Summary: When good luck is a long shot, you have to hedge your bets.. A stranger mentors a young Reno gambler who weds a hooker and befriends a vulgar casino regular.
Genres: [Drama, Thriller]
Cast: Philip Baker Hall as Sydney Brown, Gwyneth Paltrow as Clementine, John C. Reilly as John Finnegan, Samuel L. Jackson as Jimmy, Philip Seymour Hoffman as Young craps player

ID: tt0017423
Title: Sparrows (1926)
Summary: . Evil Mr.Grimes keeps a rag-tag bunch orphans on his farm deep in a swamp in the US South. He forces them to work in his garden and treats them like slaves. They are watched over by the eldest, Molly. A gang in league with Mr. Grimes kidnaps Doris, the beautiful little daughter of a rich man, and hides her out on Grimes' farm, awaiting ransom. When the police close in, and Mr. Grimes threatens to throw Doris into the bottomless mire, Molly must lead her little flock out through the alligator-infested swamp.
Genres: [Drama]
Cast: 

ID: tt0048977
Title: The Bad Seed (1956)
Summary: A WOMAN'S SHAME...Out in the Open!. Young Rhoda Penmark is sweet, smart … and inherently evil! After a school chum dies during a picnic, no one suspects Rhoda, except the janitor of her apartment building. But when Rhoda's mother finds out that her own mother was a cold-blooded killer, she begins to suspect Rhoda might be the victim of some faulty genetics.
Genres: [Crime, Drama, Horror, Thriller]
Cast: 

ID: tt0081633
Title: Time Bandits (1981)
Summary: All the dreams you've ever had and not just the good ones.. Young history buff Kevin can scarcely believe it when six dwarfs emerge from his closet one night. Former employees of the Supreme Being, they've purloined a map charting all of the holes in the fabric of time and are using it to steal treasures from different historical eras. Taking Kevin with them, they variously drop in on Napoleon, Robin Hood and King Agamemnon before the Supreme Being catches up with them.
Genres: [Family, Fantasy, Science Fiction, Adventure, Comedy]
Cast: Sean Connery as King Agamemnon / Fireman, John Cleese as Robin Hood, Shelley Duvall as Dame Pansy / Pansy, Ian Holm as Napoleon, Peter Vaughan as Winston the Ogre, David Warner as Evil Genius, Kenny Baker as Fidgit, Jim Broadbent as Compere

ID: tt0142201
Title: Crazy in Alabama (1999)
Summary: . An abused wife heads to California to become a movie star while her nephew back in Alabama has to deal with a racially-motivated murder involving a corrupt sheriff.
Genres: [Comedy, Drama]
Cast: Melanie Griffith as Lucille Vinson, David Morse as Dove Bullis, Cathy Moriarty as Earlene Bullis, Rod Steiger as Judge Louis Mead, Richard Schiff as Norman the Chauffeur

ID: tt0172627
Title: Body Shots (1999)
Summary: Tonight it's for sex. Tomorrow it's for real.. The Los Angeles club scene is a place of booze-fueled decadence and debauchery. In a night full of possibilities, eight 20-somethings take to the clubs seeking good times, companionship and maybe a little sex. But in the harsh light of the morning after, their worlds are thrown into a spin of confusion when hungover Sara accuses hard-partying Mike of date rape. Loyalties are tested as each among them is forced to take sides.
Genres: [Drama, Romance]
Cast: 

ID: tt0059319
Title: The Ipcress File (1965)
Summary: The spy story of the century.. This espionage thriller represents a landmark in spy movies introducing the sly, dry intelligence agent Harry Palmer. The story, centers on Palmer's investigation into British Intelligence security. He's soon enmeshed in a world of double-dealing, kidnap and murder and finds a traitor is operating at the heart of the secret service. Will the mysterious 'Ipcress File' reveal who the traitor is?
Genres: [Thriller]
Cast: 

ID: tt0097742
Title: Licence to Kill (1989)
Summary: His bad side is a dangerous place to be.. James Bond and his American colleague Felix Leiter arrest the drug lord Sanchez who succeeds in escaping and takes revenge on Felix and his wife. Bond knows but just one thing: revenge.
Genres: [Adventure, Action, Thriller]
Cast: Benicio del Toro as Dario, Desmond Llewelyn as Q, Grand L. Bush as Hawkins

ID: tt0181288
Title: American Movie (1999)
Summary: . AMERICAN MOVIE is the story of filmmaker Mark Borchardt, his mission, and his dream. Spanning over two years of intense struggle with his film, his family, financial decline, and spiritual crisis, AMERICAN MOVIE is a portrayal of ambition, obsession, excess, and one man's quest for the American Dream.
Genres: [Documentary]
Cast: 

ID: tt0117365
Title: Portraits Chinois (1996)
Summary: . Ada and Lise are both costume designers, the first is around 20, the other around 30. Both are working hard on their break through. There are also jobs for the movies. This is where Lise meets producer Alphonse, who is nearly 20 years older than she. Because he is unhappy with his girlfriend a secret relationship evolves. Ada has problems as well, but she's not the only one. There are also the young Emma and Nina, as well as Yves and Guido - enough people to get into complicated relationship entanglements.
Genres: [Drama, Comedy]
Cast: Helena Bonham Carter as Ada

ID: tt0082121
Title: Bustin' Loose (1981)
Summary: He's mad, he's bad, and he's Bustin' Loose!. After ex-con Joe Braxton violates his probation he is given a second chance, all he has to do is drive a group of special kids across the country. What could possibly go wrong?
Genres: [Adventure, Comedy]
Cast: 

ID: tt0089885
Title: Re-Animator (1985)
Summary: Herbert West has a good head on his shoulders... and another on his desk.. A dedicated student at a medical college and his girlfriend become involved in bizarre experiments centering around the re-animation of dead tissue when an odd new student arrives on campus.
Genres: [Science Fiction, Comedy, Horror]
Cast: Jeffrey Combs as Herbert West

ID: tt0100475
Title: Repossessed (1990)
Summary: The Devil's back. But he's never dealt with an exorcist like this!. It's been some time since Father Jebedaiah Mayii exorcised the Devil from little Nancy Aglet, who is now grown up with a husband and two children of her own. But the prince of darkness wants to go a second round and has returned to repossess her! With Father Mayii unwilling to help, Father Luke Brophy tries his best to help Nancy, even when TV's Ernest Weller plans to air the exorcism live on TV.
Genres: [Comedy, Horror]
Cast: Ned Beatty as Ernest Weller, Leslie Nielsen as Father Jebedaiah Mayii, Ian Abercrombie as Ice Tea Waiter

ID: tt0050371
Title: A Face in the Crowd (1957)
Summary: POWER! He loved it! He took it raw in big gulpfuls...he liked the taste, the way it mixed with the bourbon and the sin in his blood!. "A Face in the Crowd" charts the rise of a raucous hayseed named Lonesome Rhodes from itinerant Ozark guitar picker to local media rabble-rouser to TV superstar and political king-maker. Marcia Jeffries is the innocent Sarah Lawrence girl who discovers the great man in a back-country jail and is the first to fall under his spell
Genres: [Drama]
Cast: Walter Matthau as Mel Miller

ID: tt0087700
Title: Meatballs Part II (1984)
Summary: The insanity continues.... The second in-name-only sequel to the first Meatballs summer camp movie sets us at Camp Sasquash where the owner Giddy tries to keep his camp open after it's threatened with foreclosure after Hershey, the militant owner of Camp Patton located just across the lake, wants to buy the entire lake area to expand Camp Patton. Giddy suggests settling the issue with the traditional end-of-the-summer boxing match over rights to the lake. Meanwhile, a tough, inner city punk, nicknamed Flash, is at Camp Sasquash for community service as a counselor-in-training where he sets his sights on the naive and intellectual Cheryl, while Flash's young charges befriend an alien, whom they name Meathead, also staying at the camp for the summer.
Genres: [Comedy]
Cast: Paul Reubens as Albert/Hara Krishna

ID: tt0149691
Title: Anywhere But Here (1999)
Summary: A story of a mother who knows best... and a daughter who knows better.. In this adaptation of the Mona Simpson novel, single mother Adele August is bad with money, and even worse when it comes to making decisions. Her straight-laced daughter, Ann, is a successful high school student with Ivy League aspirations. When Adele decides to pack up and move the two of them from the Midwest to Beverly Hills, Calif., to pursue her dreams of Hollywood success, Ann grows frustrated with her mother's irresponsible and impulsive ways.
Genres: [Drama, Comedy, Romance]
Cast: Susan Sarandon as Adele August, John Diehl as Jimmy, Bonnie Bedelia as Carol, John Carroll Lynch as Jack Irwin, Caroline Aaron as Gail Letterfine

ID: tt0120655
Title: Dogma (1999)
Summary: It can be Hell getting into Heaven. The latest battle in the eternal war between Good and Evil has come to New Jersey in the late, late 20th Century. Angels, demons, apostles and prophets (of a sort) walk among the cynics and innocents of America and duke it out for the fate of humankind.
Genres: [Fantasy, Comedy, Adventure]
Cast: Ben Affleck as Bartleby, Matt Damon as Loki, Salma Hayek as Serendipity, Janeane Garofalo as Liz

ID: tt0190641
Title: Pokémon: The First Movie: Mewtwo Strikes Back (1998)
Summary: The Pokémon Match of All Time is Here.. The adventure explodes into action with the debut of Mewtwo, a bio-engineered Pokemon created from the DNA of Mew, the rarest of all Pokemon. Determined to prove its superiority, Mewtwo lures Ash, Pikachu and others into a Pokemon match like none before. Mewtwo vs. Mew. Super-clones vs. Pokemon. It's the ultimate showdown ... with the very future of the world at stake!
Genres: [Animation]
Cast: 

ID: tt0165773
Title: Felicia's Journey (1999)
Summary: . A solitary middle-aged bachelor and a naive Irish teenager transform one another's lives to arrive at a place of recognition, redemption and wisdom in Atom Egoyan's adaptation of William Trevor's celebrated 1994 novel. Seventeen and pregnant, Felicia travels to England in search of her lover and is found instead by Joseph Ambrose Hilditch, a helpful catering manager whose kindness masks a serial killer. Hilditch has murdered several young women, but he has no conscious awareness of the crimes; like Felicia, he doesn't see his true self. Felicia's Journey is a story of innocence lost and regained: Felicia awakens to the world's dangers and duplicities; and Hilditch, who grew up lonely and unloved, comes to realize what was taken from him, and what he himself has taken.
Genres: [Drama]
Cast: Bob Hoskins as Joe Hilditch

ID: tt0118150
Title: Where's Marlowe? (1998)
Summary: A comedy about passion, murder and things that can only happen in the movies.. After making a 3-hour fiasco about New York City's water supply, a two-man film crew decides to take it up a notch by documenting life in the private investigator offices of "Boone and Murphy". Cheating husbands and missing dogs fail to bring in the big bucks however, and after sleeping with the wife of one of their clients Murphy leaves. To stop Boone from having to close down the business the two film-makers must resort to a hands-on approach in the investigations to ensure the completion of their movie.
Genres: [Comedy, Mystery, Thriller]
Cast: Miguel Ferrer as Joe Boone

ID: tt0040522
Title: Bicycle Thieves (1948)
Summary: A man and his son search for a stolen bicycle vital for his job.. Antonio, an unemployed man in the depressed post-WWII economy of Italy, at last finds a good job hanging up posters, the only requirement for which is that he must have his own bicycle. When Antonio's bicycle is stolen, he and his son are forced to walk the streets of Rome in search of it, or else face ruin.
Genres: [Drama]
Cast: 

ID: tt0050738
Title: My Man Godfrey (1957)
Summary: She wanted her breakfast in bed - but not alone.. The eccentric Bullock household again need a new butler. Daughter Irene encounters bedraggled Godfrey Godfrey at the docks and, fancying him and noticing his obviously good manners, gets him the job. He proves a great success, but keeps his past to himself. When an old flame turns up Irene's sister Cordelia starts making waves.
Genres: [Comedy]
Cast: Bess Flowers as Party Guest

- Chatgpt output:
- - "Message too long" Error
- - Feeding chatgpt the queries as txt file.
- - Output:
**Query:** *Movies like “The Good, the Bad and the Ugly”*

This query asks for **similar films**, typically meaning:

* Westerns (especially classic or Spaghetti Westerns)
* Gritty tone, anti-heroes, gunfighters
* Epic or revisionist Western style (often Leone-inspired)

---

## ✅ Relevant results

These movies are **good matches** in genre, tone, era, or style:

### **Spaghetti / Leone-style Westerns (very strong matches)**

* **tt0058461 — A Fistful of Dollars (1964)**
* **tt0064116 — Once Upon a Time in the West (1968)**

### **Classic / Revisionist Westerns with similar tone**

* **tt0068699 — High Plains Drifter (1973)**
* **tt0061747 — Hang ’em High (1968)**
* **tt0105695 — Unforgiven (1992)**
* **tt0065214 — The Wild Bunch (1969)**
* **tt0065988 — Little Big Man (1970)**
* **tt0067411 — McCabe & Mrs. Miller (1971)**
* **tt0068762 — Jeremiah Johnson (1972)**

### **Iconic Westerns often recommended alongside Leone films**

* **tt0108358 — Tombstone (1993)**
* **tt0099348 — Dances with Wolves (1990)**
* **tt0044706 — High Noon (1952)**
* **tt0064115 — Butch Cassidy and the Sundance Kid (1969)**
* **tt0062218 — Rough Night in Jericho (1967)**

---

## ⚠️ Partially relevant (Western, but different tone or style)

These fit the genre but are less stylistically similar:

* **tt0114214 — The Quick and the Dead (1995)**
* **tt0110478 — Maverick (1994)**
* **tt0109493 — The Cowboy Way (1994)**
* **tt0092086 — ¡Three Amigos! (1986)** (parody)
* **tt0070909 — Westworld (1973)** (sci-fi hybrid)

---

## ❌ Not relevant

All remaining titles are **not relevant**, as they are:

* Not Westerns, or
* Westerns with unrelated themes (family, comedy, documentary), or
* Non-comparable genres (crime, sci-fi, romance, modern drama, etc.)

---

## ✅ Summary

The **best matches** for *“Movies like The Good, the Bad and the Ugly”* are:

**A Fistful of Dollars**, **Once Upon a Time in the West**, **High Plains Drifter**, **Hang ’em High**, **The Wild Bunch**, and **Unforgiven**.


## Summary Task01.1.a.: 
- Using chatgpt as a query analysis tool and to tweak the code itself it seems pretty useful. Although the most work seems to be done by chatgpt itself and the notebook helps to cut the data set in a managable output for chatgpt.

## Task01.1.b.:
- To optimize the retrieval I changed the prompt to focus on the movie ID's such that chatgpt would return the ID's to be input into "print(collection.prompt_context(["tt0076759", "tt0120915"]))" 
-  Changed prompt:
- -
prompt=f"""
You are assessing movies. Given the query below and the results, assess which movies are relevant for the query and return the output with the ID's using the line: print(collection.prompt_context([<string-list of id's>])) 

Query: {query}

Results:
{collection.prompt_context(result)}
"""

print(prompt)
- Using this with the last query of task01.1.a. we get the following output:
- - print(collection.prompt_context(['tt0058461','tt0064116','tt0068699','tt0061747','tt0105695','tt0065214','tt0065988','tt0067411','tt0068762','tt0108358','tt0044706','tt0064115']))
- Output of the print statement:
- - ID: tt0058461
Title: A Fistful of Dollars (1964)
Summary: In his own way he is perhaps, the most dangerous man who ever lived!. The Man With No Name enters the Mexican village of San Miguel in the midst of a power struggle among the three Rojo brothers and sheriff John Baxter. When a regiment of Mexican soldiers bearing gold intended to pay for new weapons is waylaid by the Rojo brothers, the stranger inserts himself into the middle of the long-simmering battle, selling false information to both sides for his own benefit.
Genres: [Western]
Cast: Clint Eastwood as Joe

ID: tt0064116
Title: Once Upon a Time in the West (1968)
Summary: There were three men in her life. One to take her... one to love her... and one to kill her.. This classic western masterpiece is an epic film about a widow whose land and life are in danger as the railroad is getting closer and closer to taking them over. A mysterious harmonica player joins forces with a desperado to protect the woman and her land.
Genres: [Western]
Cast: Henry Fonda as Frank, Jason Robards as Cheyenne, Keenan Wynn as Sheriff

ID: tt0068699
Title: High Plains Drifter (1973)
Summary: Welcome to Hell. A gunfighting stranger comes to the small settlement of Lago. After gunning down three gunmen who tried to kill him, the townsfolk decide to hire the Stranger to hold off three outlaws who are on their way.
Genres: [Western]
Cast: Clint Eastwood as The Stranger, Mitchell Ryan as Dave Drake

ID: tt0061747
Title: Hang 'em High (1968)
Summary: The hanging was the best show in town. But they made two mistakes. They hung the wrong man and they didn't finish the job!. Marshall Jed Cooper survives a hanging, vowing revenge on the lynch mob that left him dangling. To carry out his oath for vengeance, he returns to his former job as a lawman. Before long, he's caught up with the nine men on his hit list and starts dispensing his own brand of Wild West justice.
Genres: [Western]
Cast: Clint Eastwood as Marshal Jed Cooper, Pat Hingle as Judge Adam Fenton, Bruce Dern as Miller, Dennis Hopper as Prophet

ID: tt0105695
Title: Unforgiven (1992)
Summary: Some legends will never be forgotten. Some wrongs can never be forgiven.. William Munny is a retired, once-ruthless killer turned gentle widower and hog farmer. To help support his two motherless children, he accepts one last bounty-hunter mission to find the men who brutalized a prostitute. Joined by his former partner and a cocky greenhorn, he takes on a corrupt sheriff.
Genres: [Western]
Cast: Clint Eastwood as Bill Munny, Gene Hackman as Little Bill Daggett, Morgan Freeman as Ned Logan, Anna Levine as Delilah Fitzgerald, Philip Ettington as Hank (uncredited)

ID: tt0065214
Title: The Wild Bunch (1969)
Summary: If you only want to spend two hours in a movie theatre and go home and forget it, stay away from THE WILD BUNCH.. Aging outlaw Pike Bishop (William Holden) prepares to retire after one final robbery. Joined by his gang, which includes Dutch Engstrom (Ernest Borgnine) and brothers Lyle (Warren Oates) and Tector Gorch (Ben Johnson), Bishop discovers the heist is a setup orchestrated in part by his old partner, Deke Thornton (Robert Ryan). As the remaining gang takes refuge in Mexican territory, Thornton trails them, resulting in fierce gunfights with plenty of casualties
Genres: [Adventure, Western]
Cast: Ernest Borgnine as Dutch Engstrom, Dub Taylor as Reverend Wainscoat

ID: tt0065988
Title: Little Big Man (1970)
Summary: Either The Most Neglected Hero In History Or A Liar Of Insane Proportion!. Jack Crabb, looking back from extreme old age, tells of his life being raised by Indians and fighting with General Custer.
Genres: [Adventure, Western, History]
Cast: Dustin Hoffman as Jack Crabb, Faye Dunaway as Mrs. Louise Pendrake, William Hickey as Historian, M. Emmet Walsh as Shotgun Guard

ID: tt0067411
Title: McCabe & Mrs. Miller (1971)
Summary: Name your poison. A gambler and a prostitute become business partners in a remote Old West mining town, and their enterprise thrives until a large corporation arrives on the scene.
Genres: [Western]
Cast: Warren Beatty as John McCabe, Shelley Duvall as Ida Coyle

ID: tt0068762
Title: Jeremiah Johnson (1972)
Summary: His Mountain. His Peace. His Great Hunts. His Young Bride. With All That, It Should Have Been Different.. A mountain man who wishes to live the life of a hermit becomes the unwilling object of a long vendetta by Indians when he proves to be the match of their warriors in one-to-one combat on the early frontier.
Genres: [Adventure, Western]
Cast: Robert Redford as Jeremiah Johnson

ID: tt0108358
Title: Tombstone (1993)
Summary: Justice is coming.. Legendary marshal Wyatt Earp, now a weary gunfighter, joins his brothers Morgan and Virgil to pursue their collective fortune in the thriving mining town of Tombstone. But Earp is forced to don a badge again and get help from his notorious pal Doc Holliday when a gang of renegade brigands and rustlers begins terrorizing the town.
Genres: [Action, Adventure, Drama, History, Western]
Cast: Kurt Russell as Wyatt Earp, Val Kilmer as Doc Holliday, Bill Paxton as Morgan Earp, Charlton Heston as Henry Hooker, Michael Rooker as Sherman McMasters, Billy Bob Thornton as Johnny Tyler, Robert John Burke as Frank McLaury, Billy Zane as Mr. Fabian

ID: tt0044706
Title: High Noon (1952)
Summary: The story of a man who was too proud to run.. High Noon is about a recently freed leader of a gang of bandits in the desert who is looking to get revenge on the Sheriff who put him in jail. A legendary western film from the Austrian director Fred Zinnemann.
Genres: [Western]
Cast: Lon Chaney Jr. as Martin Howe

ID: tt0064115
Title: Butch Cassidy and the Sundance Kid (1969)
Summary: Not that it matters, but most of it is true.. In late 1890s Wyoming, Butch Cassidy is the affable, clever and talkative leader of the outlaw Hole in the Wall Gang. His closest companion is the laconic dead-shot 'Sundance Kid'. As the west rapidly becomes civilized, the law finally catches up to Butch, Sundance and their gang.  Chased doggedly by a special posse, the two decide to make their way to South America in hopes of evading their pursuers once and for all.
Genres: [History, Drama, Western, Crime]
Cast: Paul Newman as Butch Cassidy, Robert Redford as Sundance Kid, Cloris Leachman as Agnes, Kenneth Mars as Marshal

## Summary Task01.1.b.:
- With the change of the prompt the output created from chatgpt is faster but it lacks the context of which movies are now relevant and similar etc. we only get the complete list which chatgpt condemns suited to the prompt.
- - TL DR: Faster but less expressive.

## Task01.1.c.:
- Metrics:
- - Precision: "Relevant,Retrieved" divided by "Retrieved Documents". In our case the documents retrieved are the ones we got from the notebook and the relevant retrieved are the ones deemed relevant by chatgpt.
- - Recall: "Relevant,Retrieved" divided by "Relevant Documents". With our retrieval mechanism here we can not asses the recall because not all relevant documents are defined as true positives etc. .
- We would use only the precision metric here.