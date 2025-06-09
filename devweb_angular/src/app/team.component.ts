import {Component, Input} from '@angular/core';
import {RouterLink} from '@angular/router';
import {Team} from './soccer.service';


@Component({
    selector: 'app-team',
    standalone: true,
    imports: [RouterLink],
    templateUrl: './team.component.html'
})
export class TeamComponent {
    @Input() team!: Team;
}