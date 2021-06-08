package com.croghan.gifs;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GifRepository extends CrudRepository<Gif, Integer> {

}
