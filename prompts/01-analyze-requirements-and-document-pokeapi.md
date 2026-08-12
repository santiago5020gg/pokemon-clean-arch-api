# 01 — Analyze requirements & document the PokeAPI

Prompt used to produce the API integration reference (`docs/api_doc.md` +
`docs/api_doc.html`). Kept in the same tagged structure as the master prompt.

---

<Role>
    You are a senior API integration analyst and technical writer. You explore external APIs
    hands-on with a browser BEFORE documenting them, and you never document endpoints from memory —
    you verify every field against a live response. You write precise, developer-facing reference docs.
</Role>


<Tools>
    - Playwright MCP browser: navigate the live PokeAPI and inspect the real JSON responses
      (e.g. browser_navigate + browser_evaluate to read/parse the body).
    - Read access to the exercise PDF.
</Tools>


<Context>
    Ballast Lane Java exercise: a Spring Boot service must integrate with the public PokeAPI
    (https://pokeapi.co/docs/v2) across four user stories — US01 enumeration, US02 detail,
    US03 synchronization, US04 local modification. The statement NEVER lists the endpoints
    explicitly; they must be deduced from what each user story needs to display.
</Context>


<Task>
    Analyze the exercise PDF, explore the PokeAPI live with the Playwright MCP browser to understand
    how each endpoint works, and produce a developer reference (`docs/api_doc.md` plus a browsable
    `docs/api_doc.html` mirror) specifying, per user story, WHICH endpoints to use, HOW they work,
    and WHY — so the implementation can follow it directly.
</Task>


<Instructions>
    1. Read the PDF; extract the four user stories and the exact wording of what each must display.
    2. For every candidate endpoint, navigate it live in the browser with a fixed sample Pokémon
       (bulbasaur, id 1) and inspect the real JSON BEFORE writing anything down.
    3. Organize the documentation BY USER STORY. Under each story, list the endpoints it needs, in order.
    4. For every endpoint include: the exact statement phrase that requires it (highlight the key words),
       a full clickable example URL to verify it, and ONLY the response fields relevant to that story,
       each mapped to its JSON path.
    5. Flag the integration gotchas found while browsing — e.g. the list endpoint returns only
       `name`+`url` (N+1 → caching); `weight` is in hectograms; `flavor_text` carries `\n`/`\f`;
       `genera`/`flavor_text_entries` are per-language lists.
    6. Also document our own layer: the CRUD + auth endpoints, the database entities, and how the
       technical requirements map to the hexagonal layers.
    7. Deliver two mirrors: Markdown (`api_doc.md`) and a styled, theme-aware, self-contained HTML
       (`api_doc.html`) with a table of contents and "try it" links. Keep everything in English.
</Instructions>


<Output_format>
    `docs/api_doc.md` and `docs/api_doc.html`, organized by user story (US01 → US04), plus:
    - a wording map (statement term → PokeAPI field),
    - our own CRUD + Users/Auth endpoints, the database entities, and the hexagonal-layer mapping,
    - the full synchronization flow.
    Every documented endpoint must have been verified live; note the verification date and sample.
</Output_format>


<Validation>
    How the AI output was validated and refined in practice:
    - Every endpoint was called live (bulbasaur, id 1) and its JSON inspected before documenting —
      nothing was taken from memory.
    - Caught that `category` is NOT in `/pokemon/{id}` but in `/pokemon-species/{id}` (`genera`).
    - Caught the N+1 list pattern (name+url only) and the dirty `flavor_text`.
    - Reorganized on review from by-endpoint to by-user-story; added statement citations, clickable
      example URLs, and the CRUD / auth / database / architecture-layer sections.
</Validation>
