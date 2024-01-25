import {defs, tiny} from './examples/common.js';

const {
    Vector, Vector3, vec, vec3, vec4, color, hex_color, Shader, Matrix, Mat4, Light, Shape, Material, Scene, Texture,
} = tiny;

const {Cube, Axis_Arrows, Textured_Phong} = defs

export class Assignment4 extends Scene {
    /**
     *  **Base_scene** is a Scene that can be added to any display canvas.
     *  Setup the shapes, materials, camera, and lighting here.
     */
    constructor() {
        // constructor(): Scenes begin by populating initial values like the Shapes and Materials they'll need.
        super();

        // TODO:  Create two cubes, including one with the default texture coordinates (from 0 to 1), and one with the modified
        //        texture coordinates as required for cube #2.  You can either do this by modifying the cube code or by modifying
        //        a cube instance's texture_coords after it is already created.
        this.shapes = {
            box_1: new Cube(),
            box_2: new Cube(),
            axis: new Axis_Arrows()
        }
        console.log(this.shapes.box_1.arrays.texture_coord)


        // TODO:  Create the materials required to texture both cubes with the correct images and settings.
        //        Make each Material from the correct shader.  Phong_Shader will work initially, but when
        //        you get to requirements 6 and 7 you will need different ones.
        this.materials = {
            phong: new Material(new Textured_Phong(), {
                color: hex_color("#ffffff"),
            }),
            stars: new Material(new Texture_Rotate(), {
                color: hex_color("#000000"),
                ambient: 1, diffusivity: 0, specularity: 0,
                texture: new Texture("assets/stars.png", "NEAREST")
            }),
            earth: new Material(new Texture_Scroll_X(), {
                color: hex_color("#000000"),
                ambient: 1, diffusivity: 0, specularity: 0,
                texture: new Texture("assets/earth.gif", "LINEAR_MIPMAP_LINEAR")
            })
        }

        this.initial_camera_location = Mat4.look_at(vec3(0, 10, 20), vec3(0, 0, 0), vec3(0, 1, 0));

        this.rotating = false;
        this.prev_box_1_angle = 0;
        this.prev_box_2_angle = 0;
        this.prev_t = 0;
    }

    make_control_panel() {
        // TODO:  Implement requirement #5 using a key_triggered_button that responds to the 'c' key.
        this.key_triggered_button("Cube rotation", ["c"], () => this.rotating = !this.rotating);
    }

    display(context, program_state) {
        if (!context.scratchpad.controls) {
            this.children.push(context.scratchpad.controls = new defs.Movement_Controls());
            // Define the global camera and projection matrices, which are stored in program_state.
            program_state.set_camera(Mat4.translation(0, 0, -8));
        }

        program_state.projection_transform = Mat4.perspective(
            Math.PI / 4, context.width / context.height, 1, 100);

        const light_position = vec4(10, 10, 10, 1);
        program_state.lights = [new Light(light_position, color(1, 1, 1, 1), 1000)];

        let t = program_state.animation_time / 1000, dt = program_state.animation_delta_time / 1000;
        
        let model_transform = Mat4.identity();

        let box_1_angle;
        let box_2_angle;
        // rotation angles
        if(this.rotating) {
            // 20 rpm (2pi/3)/s
            box_1_angle = ((2 * Math.PI) / 3) * this.prev_t;
            
            // 3 rpm (pi)/s
            box_2_angle = Math.PI * this.prev_t;

            this.prev_t += dt;
        }
        else {
            box_1_angle = this.prev_box_1_angle;
            box_2_angle = this.prev_box_2_angle;
        }
        
        // TODO:  Draw the required boxes. Also update their stored matrices.
        // You can remove the folloeing line.
        //this.shapes.axis.draw(context, program_state, model_transform, this.materials.phong.override({color: hex_color("#ffff00")}));
        let box_1 = model_transform.times(Mat4.translation(-2, 0, 0)).times(Mat4.rotation(box_1_angle, 1, 0, 0));
        this.shapes.box_1.draw(context, program_state, box_1, this.materials.stars);

        
        this.shapes.box_2.arrays.texture_coord.forEach(
            (v, i, l) => v[0] = v[0] * 2
        );
        this.shapes.box_2.arrays.texture_coord.forEach(
            (v, i, l) => v[1] = v[1] * 2
        );
        let box_2 = model_transform.times(Mat4.translation(2, 0, 0)).times(Mat4.rotation(box_2_angle, 0, 1, 0));
        this.shapes.box_2.draw(context, program_state, box_2, this.materials.earth);

        this.prev_box_1_angle = box_1_angle;
        this.prev_box_2_angle = box_2_angle;
    }
}


class Texture_Scroll_X extends Textured_Phong {
    // TODO:  Modify the shader below (right now it's just the same fragment shader as Textured_Phong) for requirement #6.
    fragment_glsl_code() {
        return this.shared_glsl_code() + `
            varying vec2 f_tex_coord;
            uniform sampler2D texture;
            uniform float animation_time;
            
            void main(){
                
                float rate = -2.0 * mod(animation_time, 4.0);
                mat4 rate_mat = mat4(vec4(1.0, 0, 0, 0),
                                    vec4(0, 1.0, 0, 0),
                                    vec4(0, 0, 1.0, 0),
                                    vec4(rate, 0, 0, 1.0));
                
                vec4 tex_coord = rate_mat * (vec4(f_tex_coord, 0, 0) + vec4(1.0, 1.0, 0, 1.0));
                vec4 tex_color = texture2D(texture, tex_coord.xy);


                float u = mod(tex_coord.x, 1.0);
                float v = mod(tex_coord.y, 1.0);

                float x_to_center = u - 0.5;
                if (x_to_center > 0.25 && x_to_center < 0.35 &&
                    v > 0.15 && v < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }

                float y_to_center = v - 0.5;
                if (y_to_center > 0.25 && y_to_center < 0.35 &&
                    u > 0.15 && u < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }

                x_to_center = -x_to_center;
                if (x_to_center > 0.25 && x_to_center < 0.35 &&
                    v > 0.15 && v < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }
                
                y_to_center = -y_to_center;
                if (y_to_center > 0.25 && y_to_center < 0.35 &&
                    u > 0.15 && u < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }
                
                if( tex_color.w < .01 ) discard;
                                                                         // Compute an initial (ambient) color:
                gl_FragColor = vec4( ( tex_color.xyz + shape_color.xyz ) * ambient, shape_color.w * tex_color.w ); 
                                                                         // Compute the final color with contributions from lights:
                gl_FragColor.xyz += phong_model_lights( normalize( N ), vertex_worldspace );
        } `;
    }
}


class Texture_Rotate extends Textured_Phong {
    // TODO:  Modify the shader below (right now it's just the same fragment shader as Textured_Phong) for requirement #7.
    fragment_glsl_code() {
        return this.shared_glsl_code() + `
            varying vec2 f_tex_coord;
            uniform sampler2D texture;
            uniform float animation_time;
            void main(){
                float half_PI = 0.5 * 3.1415;
                float rate = half_PI * mod(animation_time, 4.0);
                mat4 rate_mat = mat4(vec4(cos(rate), sin(rate), 0, 0),
                                    vec4(sin(rate), -cos(rate), 0, 0),
                                    vec4(0, 0, 1.0, 0),
                                    vec4(0, 0, 0, 1.0));
                
                vec4 tex_coord = (rate_mat * (vec4(f_tex_coord, 0, 0) + vec4(-0.5, -0.5, 0, 0))) + vec4(0.5, 0.5, 0, 0);
                vec4 tex_color = texture2D(texture, tex_coord.yx);


                float u = mod(tex_coord.x, 1.0);
                float v = mod(tex_coord.y, 1.0);

                float x_to_center = u - 0.5;
                if (x_to_center > 0.25 && x_to_center < 0.35 &&
                    v > 0.15 && v < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }

                float y_to_center = v - 0.5;
                if (y_to_center > 0.25 && y_to_center < 0.35 &&
                    u > 0.15 && u < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }

                x_to_center = -x_to_center;
                if (x_to_center > 0.25 && x_to_center < 0.35 &&
                    v > 0.15 && v < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }
                
                y_to_center = -y_to_center;
                if (y_to_center > 0.25 && y_to_center < 0.35 &&
                    u > 0.15 && u < 0.85) {
                    tex_color = vec4(0, 0, 0, 1.0);
                }


                if( tex_color.w < .01 ) discard;
                                                                         // Compute an initial (ambient) color:
                gl_FragColor = vec4( ( tex_color.xyz + shape_color.xyz ) * ambient, shape_color.w * tex_color.w ); 
                                                                         // Compute the final color with contributions from lights:
                gl_FragColor.xyz += phong_model_lights( normalize( N ), vertex_worldspace );
        } `;
    }
}