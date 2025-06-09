import {Routes} from '@angular/router';
import {HomeComponent} from './home.component';
import {TeamDetailComponent} from './team-detail.component';

export const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'team/:teamId', component: TeamDetailComponent}
];
