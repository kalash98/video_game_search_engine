package fr.lernejo.search.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GameController {


    private final GameSearchService gameSearchService;

    public GameController(GameSearchService gameSearchService) {
        this.gameSearchService = gameSearchService;
    }
    @GetMapping("/api/games")
    public ResponseEntity<List<Map<String, Object>>> searchGames(@RequestParam String query, @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(gameSearchService.searchGames(query, size));
    }
}
