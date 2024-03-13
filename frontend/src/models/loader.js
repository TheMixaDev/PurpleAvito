/**
 * Class for imitating realistic loading.
 * It calculates the progress based on a mathematical function and can end the loading at a specific time.
 *
 * The function used is f(x) = sqrt(1.5x) if <= ln(x) + 2e, otherwise f(x) = ln(x) + 2e
 *
 * @property {function} callback The function to call when the loading finishes
 * @property {function} progressFunction The function to call to update the progress
 * @property {number} referencedTime The time used as reference in the mathematical function
 * @property {number} msInLnTime The time it takes for the function to go from 0 to ln(x)
 * @property {number} newMsInLnTime The time it takes for the function to go from ln(x) to the finish (1)
 * @property {number} breakTime The time at which the break in the function occurs
 * @property {number} startTime The time the loading started
 * @property {number} lastUpdate The time the progress was last updated
 * @property {boolean} canEnd If the loading can end
 * @property {boolean} ended If the loading has ended
 * @property {number} offsetX The offset used to calculate the finish function
 * @property {number} offsetY The offset used to calculate the finish function
 */
export class Loader {
    constructor(callback, progressFunction, referencedTime) {
        this.callback = callback;
        this.progressFunction = progressFunction;
        this.referencedTime = referencedTime;

        this.msInLnTime = 60.708/this.referencedTime;
        this.newMsInLnTime = 0;
        this.breakTime = 0;

        this.startTime = -1;
        this.lastUpdate = -1;
        this.canEnd = false;
        this.ended = false;
    }

    /**
     * Returns the current X value based on the break time, start time, and time conversion factors.
     *
     * @return {number} The current X value.
     */
    get currentX() {
        return ((!this.breakTime ? Date.now() : this.breakTime) - this.startTime) * this.msInLnTime +
                (!this.breakTime ? 0 : (Date.now() - this.breakTime) * this.newMsInLnTime);
    }

    /**
     * Return the minimum value between the square function and the natural log function by the ln time.
     *
     * @param {type} logTime - The log time to be converted
     * @return {type} The minimum value between the square function and the natural log function
     */
    fromLnTime(logTime) {
        return this.squareFunction(logTime) < this.lnFunction(logTime) ? this.squareFunction(logTime) : this.lnFunction(logTime);
    }

    /**
     * Calculate the percentage from log time.
     *
     * @param {type} logTime - the log time to calculate percentage from
     * @return {type} the percentage calculated from the log time
     */
    percentFromLnTime(logTime) {
        return this.fromLnTime(logTime) / (Math.E * 4);
    }

    /**
     * Square root function of 1.5 times the input.
     *
     * @param {number} x - the input value
     * @return {number} the calculated square root
     */
    squareFunction(x) {
        return Math.sqrt(1.5 * x);
    }

    /**
     * The natural logarithm function. ln(x) + 2e
     *
     * @param {number} x - The number to calculate the natural logarithm of
     * @return {number} The result of the natural logarithm calculation plus 2 times Euler's number
     */
    lnFunction(x) {
        return Math.log(x) + 2 * Math.E;
    }

    /**
     * The finish function. ((x+x0)^1.5+y0) / 4e
     *
     * @param {number} x - the input value for the calculation
     * @return {number} the result of the mathematical expression
     */
    finishFunction(x) {
        return (Math.pow(x + this.offsetX, 1.5) + this.offsetY) / (Math.E * 4);
    }

    /**
     * Reverse finish function. (4ey-y0)^(2/3)-x0
     *
     * @param {number} y - the input value for the calculation
     * @return {number} the result of the mathematical expression
     */
    reverseFinishFunction(y) {
        return Math.pow(Math.E * 4 * y - this.offsetY, 2/3) - this.offsetX
    }

    /**
     * Executes a step in a process until completion. 
     */
    tick() {
        if(this.ended) return;
        let percent = this.canEnd ?
                        this.finishFunction(this.currentX) :
                        this.percentFromLnTime(this.currentX);
        if(percent >= 1 && this.canEnd) {
            percent = 1;
            this.ended = true;
            return this.callback(this.data);
        } else {
            setTimeout(this.tick.bind(this), 10);
            this.progressFunction(Math.max(Math.min(percent, 1),0)*100);
        }
    }

    /**
     * A method to end the process with optional data.
     *
     * @param {any} data - The optional data to be passed.
     */
    end(data = null) {
        this.canEnd = true;
        this.data = data;
        if(this.startTime < 0) {
            this.ended = true;
            return this.callback(data);
        }
        this.offsetX = -this.currentX;
        this.offsetY = this.fromLnTime(this.currentX);
        console.log(this.reverseFinishFunction(1))
        console.log(this.currentX)
        console.log(this.msInLnTime)
        let remainingTime = (this.reverseFinishFunction(1) - this.currentX) / this.msInLnTime;
        if(remainingTime > 1500) {
            this.newMsInLnTime = remainingTime / 1500 * this.msInLnTime;
            this.breakTime = Date.now();
        }
        console.log(remainingTime);
    }

    /**
     * The start function initiates a timer and sets up a recurring task.
     */
    start() {
        if(this.canEnd) return;
        this.startTime = Date.now();
        this.lastUpdate = this.startTime;
        setTimeout(this.tick.bind(this), 10);
    }
}
