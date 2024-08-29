package com.croghan.gifs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GifRepository extends JpaRepository<Gif, Integer> {

    @Query(value = "SELECT * FROM Gif WHERE posted = false ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Gif findRandomUnpostedGif();

    // This method will retrieve the most recently added Gif
    Gif findTopByOrderByIdDesc();

}
