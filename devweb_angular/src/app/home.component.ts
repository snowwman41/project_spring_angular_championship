import {Component} from '@angular/core';
import {SoccerService, RankingRow} from './soccer.service';
import {OnInit} from '@angular/core';
import {RouterLink} from '@angular/router';
//import {TeamComponent } from './team.component';
import {RankingComponent} from './ranking.component';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [RouterLink, RankingComponent],
    templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

    ranking: RankingRow[] = [];

    constructor(private soccerService: SoccerService) {
    }


    ngOnInit() {
        this.soccerService.getRanking().subscribe(ranking => this.ranking = ranking);
    }
}