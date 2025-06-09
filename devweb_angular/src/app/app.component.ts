import {Component} from '@angular/core';
import {HomeComponent} from './home.component';
import {RouterOutlet} from '@angular/router';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [HomeComponent, RouterOutlet],
    //imports: [RouterOutlet],
    templateUrl: './app.component.html'
})
export class AppComponent {

}