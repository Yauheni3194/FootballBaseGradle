package by.kovalevskiy.FootballBaseGradle.controller;

import by.kovalevskiy.FootballBaseGradle.exception.PlaceAlreadyExistsException;
import by.kovalevskiy.FootballBaseGradle.model.Place;
import by.kovalevskiy.FootballBaseGradle.services.PlaceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping()
    public String showAllPlaces(Model model) {
        model.addAttribute("places", placeService.showAllPlaces());
        return "place/allPlaces";
    }

    @GetMapping("/{id}")
    public String showGame(@PathVariable("id") int id, Model model) {
        model.addAttribute("place", placeService.showPlace(id));
        return "place/place";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newPlace(Model model) {
        model.addAttribute("place", new Place());
        return "place/newPlace";
    }
    @PostMapping()
    public String createPlace (@ModelAttribute("place") @Valid Place place, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "place/newPlace";
        }
        try {
            placeService.createPlace(place);
        }catch (PlaceAlreadyExistsException e){
            bindingResult.reject("PlaceAlreadyExistsException",
                    "Такое место проведения уже существует");
            return "place/newPlace";
        }

        return "redirect:/places";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping ("/{id}/delete")
    public String deletePlace (@PathVariable("id") int id){
        placeService.deletePlace(id);
        return "redirect:/places";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping ("/{id}/edit")
    public String editPlace (Model model, @PathVariable("id") int id){
        model.addAttribute("place", placeService.showPlace(id));
        return "place/editPlace";
    }

    @PostMapping ("/{id}/edit")
    public String updatePlace (@ModelAttribute("place") @Valid Place place, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "place/editPlace";
        }
        try {placeService.updatePlace(place);}
        catch (PlaceAlreadyExistsException e) {
            bindingResult.reject("PlaceAlreadyExistsException",
                    "Такое место проведения уже существует");
            return "place/editPlace";
        }
        return "redirect:/places";
    }
}
