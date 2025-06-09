import {Component, Input} from '@angular/core';
import {RouterLink} from '@angular/router';
import {RankingRow} from './soccer.service';
import {TeamComponent} from './team.component';


@Component({
    selector: 'app-ranking',
    standalone: true,
    imports: [RouterLink, TeamComponent],
    templateUrl: './ranking.component.html'
})
export class RankingComponent {
    @Input() ranking: RankingRow[] = [];
}