package com.croghan.gifs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Random;

@RestController
public class GifController {
    @Autowired
    public GifRepository gifRepository;

    @PostMapping("/createGif")
    public ResponseEntity<Gif> createGif(@RequestBody Gif newGif) {
        if (gifRepository.existsById(newGif.getId())) {
            System.out.println("Creation Failed. There is already a gif with that id.");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        gifRepository.save(newGif);
        System.out.println("Creation Success.");
        return new ResponseEntity<>(newGif, HttpStatus.CREATED);
    }

    @GetMapping("/getGif/{id}")
    public ResponseEntity<Gif> getGif(@PathVariable("id") Integer id) {
        System.out.println("Received request for GIF with ID: " + id);
        return gifRepository.findById(id)
                .map(gif -> new ResponseEntity<>(gif, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/getAllGifs")
    public ResponseEntity<List<Gif>> getAllGifs() {
        List<Gif> gifList = (List<Gif>) gifRepository.findAll();
        return new ResponseEntity<>(gifList, HttpStatus.OK);
    }


    @GetMapping("/getRandomGif")
    public ResponseEntity<Gif> getRandomGif() {
        long count = gifRepository.count();

        if (count == 0) {
            System.out.println("Retrieval failed. There are no gifs in the database.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        int randomIndex = new Random().nextInt((int) count);
        Page<Gif> gifsPage = gifRepository.findAll(PageRequest.of(randomIndex, 1));
        Gif randomGif = gifsPage.stream().findFirst().orElse(null);

        return new ResponseEntity<>(randomGif, HttpStatus.OK);
    }

    @GetMapping("/getRandomUnpostedGif")
    public ResponseEntity<Gif> getRandomUnpostedGif() {
        Gif randomGif = gifRepository.findRandomUnpostedGif();

        if (randomGif == null) {
            System.out.println("Retrieval failed. There are no unposted gifs in the database.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(randomGif, HttpStatus.OK);
    }

    @Async
    @GetMapping("/getMostRecentGif")
    public ResponseEntity<Gif> getMostRecentGif() {
        Gif latestGif = gifRepository.findTopByOrderByIdDesc();

        if (latestGif == null) {
            System.out.println("Retrieval failed. There are no gifs in the database.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(latestGif, HttpStatus.OK);
    }

    @GetMapping("/getDatabaseCount")
    public ResponseEntity<Long> getDatabaseCount() {
        long count = gifRepository.count();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PutMapping("/updateGif")
    public ResponseEntity<Gif> updateGif(@RequestBody Gif newGif) {
        // Check if the GIF exists
        if (!gifRepository.existsById(newGif.getId())) {
            System.out.println("Update failed. There is no gif in the database with id " + newGif.getId() + ".");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Save the updated GIF
        Gif updatedGif = gifRepository.save(newGif);
        System.out.println("The gif has been updated.");
        return new ResponseEntity<>(updatedGif, HttpStatus.OK);
    }

    @DeleteMapping("/deleteGif/{id}")
    public ResponseEntity<String> deleteGif(@PathVariable("id") int id) {
        // Check if the GIF exists
        if (!gifRepository.existsById(id)) {
            System.out.println("Deletion failed. There is no gif with id " + id + ".");
            return new ResponseEntity<>("Gif not found.", HttpStatus.NOT_FOUND);
        }

        // Delete the GIF
        gifRepository.deleteById(id);
        System.out.println("The gif with id " + id + " has been deleted.");
        return new ResponseEntity<>("Gif deleted successfully.", HttpStatus.OK);
    }

}
