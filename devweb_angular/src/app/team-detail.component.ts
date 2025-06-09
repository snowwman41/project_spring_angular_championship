import {Component, Input, OnChanges} from "@angular/core";
import {RouterLink} from '@angular/router';
import {Match, RankingRow, SoccerService} from "./soccer.service";
import {DatePipe} from "@angular/common";
import {TeamComponent} from './team.component';
import {RankingComponent} from './ranking.component';
import {FormsModule} from '@angular/forms';


@Component({
    selector: 'app-team-detail',
    standalone: true,
    imports: [RouterLink, DatePipe, TeamComponent, RankingComponent, FormsModule],
    templateUrl: './team-detail.component.html'
})
export class TeamDetailComponent implements OnChanges {
    @Input()
    public teamId!: string;
    ranking: RankingRow[] = [];
    matches: Match[] = [];
    public showWins: boolean = true;
    public showDraws: boolean = true;
    public showLosses: boolean = true;

    constructor(private soccerService: SoccerService) {
    }

    ngOnChanges() {
        this.soccerService.getRankingRow(this.teamId).subscribe(row => this.ranking = [row]);
        this.soccerService.getTeamMatches(this.teamId).subscribe(matches => this.matches = matches);

    }

    filterMatches(): Match[] {
        const matchesSelect: Match[] = [];
        for (let match of this.matches) {
            if (this.showWins) {
                if ((match.homeTeam.id == this.teamId) && ((match.homeTeamGoals - match.awayTeamGoals) > 0)) {
                    matchesSelect.push(match);
                } else if ((match.awayTeam.id == this.teamId) && ((match.homeTeamGoals - match.awayTeamGoals) < 0)) {
                    matchesSelect.push(match);
                }
            }
            if (this.showDraws) {
                if ((match.homeTeam.id == this.teamId || match.awayTeam.id == this.teamId) && (match.homeTeamGoals == match.awayTeamGoals)) {
                    matchesSelect.push(match);
                }
            }
            if (this.showLosses) {
                if ((match.homeTeam.id == this.teamId) && (match.homeTeamGoals < match.awayTeamGoals)) {
                    matchesSelect.push(match);
                } else ((match.awayTeam.id == this.teamId) && (match.homeTeamGoals > match.awayTeamGoals)) //{
                matchesSelect.push(match);
                //}
            }
        }
        return matchesSelect;
    }
}