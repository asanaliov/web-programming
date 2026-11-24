# Lab 2 – guide for extra requirements

Every change goes through the same layers. Work bottom-up and compile after each step:

```
model → DataHolder → repository → service → controller → template
```

| Requirement sounds like...              | Section |
|-----------------------------------------|---------|
| "add attribute X to team/league"        | 1       |
| "filter / search / sort teams"          | 2       |
| "don't allow X" / "show error when X"   | 3       |
| "remove player from team"               | 4       |
| "CRUD for leagues" / new entity         | 5       |
| "page that shows league and its teams"  | 6       |
| "show count / total / extra info"       | 7       |
| something breaks                        | 8       |

---

## 1. Add a field (example: `Integer foundedYear` on `Team`)

1. **`Team.java`**: add the field **at the end**. `@AllArgsConstructor` takes the fields in declaration order, so add it to the custom (random id) constructor too.
   ```java
   private Integer foundedYear;
   ```
2. **`DataHolder.java`**: pass a value in every `new Team(...)`.
3. **`TeamService` + `TeamServiceImpl`**: add the param to `create` and `update`, set it, and validate it if needed:
   ```java
   if (foundedYear == null || foundedYear < 1800) {
       throw new IllegalArgumentException("Invalid founded year");
   }
   ```
4. **`TeamController`**: add it to `saveTeam` and `editTeam` and pass it on.
   ```java
   @RequestParam(required = false) Integer foundedYear
   ```
   With `required = false` an empty input gives `null`, so the **service** shows a nice error. With the default `required = true` an empty input is a **400 Bad Request**.
5. **`team-form.html`**: the `name` must match the `@RequestParam` name exactly.
   ```html
   <label for="foundedYear">Founded year</label>
   <input type="number" id="foundedYear" name="foundedYear" th:value="${team?.foundedYear}">
   ```
6. **`listTeams.html`**: add a `<th>Founded</th>` and `<td th:text="${t.foundedYear}"></td>`.

---

## 2. Filter / search / sort teams

**Service** (`TeamService` + `TeamServiceImpl`):
```java
@Override
public List<Team> findByLeague(Long leagueId) {
    return teamRepository.findAll().stream()
            .filter(t -> t.getLeague().getId().equals(leagueId))
            .toList();
}

@Override
public List<Team> searchByName(String text) {
    return teamRepository.findAll().stream()
            .filter(t -> t.getName().toLowerCase().contains(text.toLowerCase()))
            .toList();
}
```

**Controller**: extend `getTeamsPage`:
```java
@GetMapping("/teams")
public String getTeamsPage(@RequestParam(required = false) String errorMessage,
                           @RequestParam(required = false) Long leagueId,
                           Model model) {
    List<Team> teams = leagueId == null ? teamService.listTeams() : teamService.findByLeague(leagueId);
    model.addAttribute("teams", teams);
    model.addAttribute("leagues", leagueService.listLeagues());
    model.addAttribute("selectedLeagueId", leagueId);
    model.addAttribute("errorMessage", errorMessage);
    return "listTeams";
}
```

**Template** (`listTeams.html`): a GET form, so the filter ends up in the URL (`/teams?leagueId=2`):
```html
<form action="/teams" method="GET">
    <select name="leagueId">
        <option value="">All leagues</option>
        <option th:each="l : ${leagues}" th:value="${l.id}" th:text="${l.name}"
                th:selected="${l.id == selectedLeagueId}"></option>
    </select>
    <input type="submit" value="Filter">
</form>
```
`value=""` becomes `null` in the controller, which means "show all".

**Sorting**: add to the stream before `.toList()`:
```java
.sorted(Comparator.comparing(Team::getName))                                   // A → Z
.sorted(Comparator.comparingInt((Team t) -> t.getPlayers().size()).reversed()) // most players first
```

---

## 3. Business rule ("don't allow X")

Throw an `IllegalArgumentException` with a message **in the service**. The controllers already catch it and redirect with `errorMessage`, so nothing else changes.

```java
// max 5 players per team (in addPlayerToTeam)
if (team.getPlayers().size() >= 5) {
    throw new IllegalArgumentException("Team " + team.getName() + " already has 5 players");
}

// unique team name (in create)
if (teamRepository.findAll().stream().anyMatch(t -> t.getName().equalsIgnoreCase(name))) {
    throw new IllegalArgumentException("Team " + name + " already exists");
}

// unique team name (in update) – skip the team being edited
if (teamRepository.findAll().stream().anyMatch(t -> !t.getId().equals(id) && t.getName().equalsIgnoreCase(name))) {
    throw new IllegalArgumentException("Team " + name + " already exists");
}
```

If the rule is on a method whose controller **doesn't** catch yet (e.g. `deleteTeam`), wrap it the same way as `saveTeam`:
```java
try {
    teamService.deleteById(id);
    return "redirect:/teams";
} catch (IllegalArgumentException e) {
    return "redirect:/teams?errorMessage=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
}
```

---

## 4. Remove a player from a team

**Service**:
```java
@Override
public Team removePlayerFromTeam(Long teamId, String playerId) {
    Team team = findById(teamId);
    team.getPlayers().removeIf(p -> p.getPlayerId().equals(playerId));
    return teamRepository.save(team);
}
```

**Controller** (`TeamDetailsController`):
```java
@PostMapping("/teamDetails/remove")
public String removePlayer(@RequestParam Long teamId, @RequestParam String playerId) {
    teamService.removePlayerFromTeam(teamId, playerId);
    return "redirect:/teamDetails?teamId=" + teamId;
}
```

**Template** (`teamDetails.html`): the `<li>` now has content inside, so swap its `th:text` for a `<span>`:
```html
<li th:each="p : ${team.players}">
    <span th:text="${p.firstName} + ' ' + ${p.lastName} + ' (#' + ${p.jerseyNumber} + ')'"></span>
    <form style="display: inline" action="/teamDetails/remove" method="POST">
        <input type="hidden" name="teamId" th:value="${team.id}">
        <input type="hidden" name="playerId" th:value="${p.playerId}">
        <input type="submit" value="Remove">
    </form>
</li>
```

---

## 5. CRUD for another entity (example: leagues)

Copy the Team pattern 1:1 and rename. Checklist:

- [ ] `League.java`: constructor without id: `this.id = (long) (Math.random() * 1000);`
- [ ] `LeagueRepository` + impl: add `save` and `deleteById` (copy from `InMemoryTeamRepository`)
- [ ] `LeagueService` + impl: `findById`, `create`, `update`, `deleteById`, all with validation
- [ ] `LeagueController`: same 6 methods as `TeamController`

  | Method | Path                          | Returns                 |
  |--------|-------------------------------|-------------------------|
  | GET    | `/leagues`                    | `listLeagues`           |
  | GET    | `/leagues/league-form`        | `league-form`           |
  | GET    | `/leagues/league-form/{id}`   | `league-form`           |
  | POST   | `/leagues/add`                | `redirect:/leagues`     |
  | POST   | `/leagues/edit/{id}`          | `redirect:/leagues`     |
  | POST   | `/leagues/delete/{id}`        | `redirect:/leagues`     |
- [ ] templates: copy `listTeams.html` → `listLeagues.html`, `team-form.html` → `league-form.html`
- [ ] link to `/leagues` from `listTeams.html`

**Deleting a league that has teams** leaves those teams pointing at a league that no longer exists. Usually you refuse instead (inject `TeamRepository` into `LeagueServiceImpl`, then use the `try/catch` from section 3 in the controller):
```java
if (teamRepository.findAll().stream().anyMatch(t -> t.getLeague().getId().equals(id))) {
    throw new IllegalArgumentException("League has teams and cannot be deleted");
}
```

**Editing a league** updates it on every team automatically, because teams hold a reference to the same `League` object.

---

## 6. Details page (example: league + its teams)

```java
@GetMapping("/leagues/{id}")
public String getLeagueDetails(@PathVariable Long id, Model model) {
    League league;
    try {
        league = leagueService.findById(id);
    } catch (IllegalArgumentException e) {
        return "redirect:/leagues?errorMessage=LeagueNotFound";
    }
    model.addAttribute("league", league);
    model.addAttribute("teams", teamService.findByLeague(id));
    return "leagueDetails";
}
```
`/leagues/league-form` and `/leagues/{id}` don't clash, because Spring prefers the literal path.

Link to it: `<a th:href="@{/leagues/{id}(id=${l.id})}" th:text="${l.name}"></a>`

---

## 7. Show extra info (Thymeleaf cheat sheet)

| Need                       | Code |
|----------------------------|------|
| show only if               | `th:if="${x != null}"` / `th:unless="${...}"` |
| empty list                 | `${#lists.isEmpty(team.players)}` |
| count                      | `${team.players.size()}` |
| null-safe + default        | `${t.league?.name ?: 'No league'}` |
| link with query param      | `th:href="@{/players(teamId=${t.id})}"` |
| link with path variable    | `th:href="@{/teams/team-form/{id}(id=${t.id})}"` |
| preselect option           | `th:selected="${team?.league?.id == l.id}"` |
| add or edit in one form    | `th:action="${team} ? @{/teams/edit/{id}(id=${team.id})} : @{/teams/add}"` |

Totals and other computed values go **in the controller (or service)**, not the template:
```java
model.addAttribute("totalPlayers",
        teamService.listTeams().stream().mapToInt(t -> t.getPlayers().size()).sum());
```

---

## 8. Something breaks

| You see                                         | Usual cause |
|-------------------------------------------------|-------------|
| **400 Bad Request**                             | required `@RequestParam` missing/empty, input `name` ≠ param name, or text sent to a `Long` |
| **405 Method Not Allowed**                      | form `method="GET"` but controller has `@PostMapping` (or the reverse) |
| **500 "template might not exist"**              | return value ≠ file name in `templates/` (no `.html`, no leading `/`) |
| **500 SpelEvaluationException ... null**        | template reads a field of something not in the model. Add it, or use `?.` |
| app won't start: **Ambiguous mapping**          | two methods with the same path + HTTP method |
| app won't start: **No qualifying bean**         | missing `@Service` / `@Repository` on an impl class |
| `DataHolder` doesn't compile after a new field  | `@AllArgsConstructor` param order = field order |
| request lands on the old servlet                | servlet still on the same path as the controller |
| refresh re-submits the form                     | POST method returns a view instead of `redirect:` |
