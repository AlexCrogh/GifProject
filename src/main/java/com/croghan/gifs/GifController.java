package com.croghan.gifs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Random;

@RestController
@Controller
@Service
public class GifController {
    @Autowired
    public GifRepository gifRepository;

    @Async
    @RequestMapping(value = "/createGif", method = RequestMethod.POST)
    public Gif createGif(@RequestBody Gif newGif){
        try {
            if (getGif(newGif.getId()) == null){
                gifRepository.save(newGif);
                System.out.println("Creation Success.");
                return newGif;
        }else {
                System.out.println("Creation Failed. There is already a gif with that id.");
                return null;
            }
        }catch (Exception e){
            System.out.println("Could not create gif. Read error.");
            System.out.println(e.toString());
            return null;
        }
    }

    @Async
    @RequestMapping(value = "/getGif/{id}", method = RequestMethod.GET)
    public Gif getGif(@PathVariable("id") Integer id) {

        try {
            Gif gif = gifRepository.findById(id).get();
            return gif;
        }catch(Exception e){
            System.out.println("Retrieval failed. There is no gif with id "+ id +".");
            return null;
        }
    }
    @Async
    @RequestMapping(value = "/getAllGifs", method = RequestMethod.GET)
    public ArrayList<Gif> getAll(){
        Iterable<Gif> gifs = gifRepository.findAll();
        ArrayList<Gif> gifList = new ArrayList<Gif>();
        gifs.iterator().forEachRemaining(gifList::add);
        return gifList;
    }

    @Async
    @RequestMapping(value = "/getRandomGif", method = RequestMethod.GET)
    public Gif getGif() {
        try {
            Random rand = new Random();
            int count = (int) gifRepository.count();
            int num = rand.nextInt(count);
            ArrayList<Gif> gifs = getAll();
            return gifs.get(num);
        }
        catch(Exception e) {
            System.out.println("Retrieval failed. There are no gifs in the database.");
            return null;
        }
    }

    @Async
    @RequestMapping(value = "/getTwitterGif", method = RequestMethod.GET)
    public Gif getTwitterGif() {
        Random rand = new Random();
        ArrayList<Gif> gifs= getAll();
        if(gifRepository.count()>0) {
            for (int i = 0; i < gifs.size(); i++) {
                if (gifs.get(i).isPosted())
                    gifs.remove(i);
            }
            if(gifs.size()>0) {
                int num = rand.nextInt(gifs.size());
                return gifs.get(num);
            }else
                System.out.println("Retrieval failed. All gifs in the database have already been posted.");
                return null;
        }
        else{
            System.out.println("Retrieval failed. There are no gifs in the database.");
            return null;
        }
    }

    @Async
    @RequestMapping(value = "/getMostRecentGif", method = RequestMethod.GET)
    public Gif getMostRecentGif() {
        try {
            int dbCount = (int) gifRepository.count();
            Gif gif = gifRepository.findById(dbCount).get();
            return gif;
        }
        catch(Exception e){
            System.out.println("Retrieval failed. There are no gifs in the database.");
            return null;
        }
    }

    @RequestMapping(value = "/getDatabaseCount", method = RequestMethod.GET)
    public long getDatabaseCount(){
        return gifRepository.count();
    }

    @RequestMapping(value = "/updateGif", method = RequestMethod.PUT)
    public Gif updateGif(@RequestBody Gif newGif) {

        boolean updated =false;
        for(Gif gif : gifRepository.findAll()){
            if(gif.getId() == newGif.getId()){
                gif = newGif;
                gifRepository.save(gif);
                updated = true;
                System.out.println("The gif has been updated.");
                break;
            }
        }
        if(!updated)
            System.out.println("Update failed. There is no gif in the database with id " + newGif.getId() + ".");
        return newGif;
    }

    @RequestMapping(value = "/deleteGif/{id}", method = RequestMethod.DELETE)
    public void deletePost(@PathVariable("id") int id) {
        boolean deleted = false;
        for(Gif gif : gifRepository.findAll()){
            if(gif.getId() == id){
                gifRepository.delete(gif);
                deleted = true;
            }
        }
        if(deleted)
            System.out.println("The gif with id " + id + " has been deleted.");
        else
            System.out.println("Deletion failed. There is no gif with id " + id + "." );
    }
}
